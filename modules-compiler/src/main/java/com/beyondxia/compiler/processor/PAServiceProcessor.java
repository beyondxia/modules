package com.beyondxia.compiler.processor;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;
import com.google.auto.service.AutoService;
import com.beyondxia.compiler.utils.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static com.beyondxia.compiler.utils.Constants.REGISTER_METHOD_NAME;
import static com.beyondxia.compiler.utils.Constants.REGISTER_PACKAGE_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Create by ChenWei on 2018/8/24 15:49
 **/
@AutoService(Processor.class)
//@SupportedOptions(MODULE_NAME)
public class PAServiceProcessor extends AbstractProcessor {

    private Filer mFiler;
//    private String moduleName;

    private HashMap<String, List<ExecutableElement>> methodMap = new HashMap<>();
    private Elements mElementUtils;
    private File mPath = new File("./modules-services-api/src/main/java");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();

//        Map<String, String> options = processingEnv.getOptions();
//        if (MapUtils.isNotEmpty(options)) {
//            moduleName = options.get(MODULE_NAME);
//        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ExportMethod.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> methodElements = roundEnvironment.getElementsAnnotatedWith(ExportMethod.class);
        if (methodElements == null || methodElements.isEmpty()) {
            return false;
        }
        try {
            //将注解按照类分类
            classifyMethodMap(methodElements);
            //生成library库中的文件
            generateLibraryResource();
            //生成自动注册service的文件
            generateBuildResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void generateLibraryResource() throws Exception {
        Set<String> keySet = methodMap.keySet();
        for (String fullClassName : keySet) {
            List<ExecutableElement> elementList = methodMap.get(fullClassName);
            generateInterfaceSource(fullClassName, elementList);
        }
    }

    private void generateInterfaceSource(String fullClassName, List<ExecutableElement> elementList) throws Exception {
        String packageName = ClassName.get((TypeElement) elementList.get(0).getEnclosingElement()).packageName();
        //interface
        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(getInterfaceSourceName(getClassSimpleNameByFullName(fullClassName)))
                .addJavadoc("Auto generate by apt, don't edit")
                .addModifiers(PUBLIC);

        //添加成员变量
        FieldSpec.Builder filedSpecBuilder = FieldSpec.builder(String.class, "SERVICE_NAME");
        filedSpecBuilder.addModifiers(PUBLIC, STATIC, FINAL).initializer("$S", packageName + "." + getInterfaceSourceName(getClassSimpleNameByFullName(fullClassName)));
        interfaceBuilder.addField(filedSpecBuilder.build());

        for (ExecutableElement executableElement : elementList) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                    .addModifiers(PUBLIC, ABSTRACT);
            methodBuilder.returns(TypeName.get(executableElement.getReturnType()));

            List<? extends VariableElement> parameters = executableElement.getParameters();
            for (VariableElement parameter : parameters) {
                //入参名称
                ParameterSpec interfaceParamSpec = ParameterSpec.builder(ClassName.get(parameter.asType()), parameter.getSimpleName().toString()).build();
                methodBuilder.addParameter(interfaceParamSpec);
            }

            //注册服务方法
            interfaceBuilder.addMethod(methodBuilder.build());
        }

        JavaFile.builder(packageName, interfaceBuilder.build()).build().writeTo(mPath);

        //class
        createClassSourceFile(packageName, getClassSimpleNameByFullName(fullClassName));
    }

    private void createClassSourceFile(String packageName, String className) throws IOException {
        StringBuilder sb = new StringBuilder("package " + packageName + ";\n")
                .append("import com.beyondxia.modules.PAService;\n")
                .append("import com.beyondxia.modules.exception.NoServiceException;\n\n")
                .append("public abstract class " + getServiceClassSourceName(className) + " extends PAService implements " + getInterfaceSourceName(className) + " {\n")
                .append("\tpublic static " + getInterfaceSourceName(className) + " get() {\n")
                .append("\t\ttry{\n")
                .append("\t\t\treturn getService(SERVICE_NAME);\n")
                .append("\t\t} catch (NoServiceException noServiceException) {\n")
                .append("\t\t\tnoServiceException.printStackTrace();\n")
                .append("\t\t}\n")
                .append("\t\treturn null;\n")
                .append("\t}\n")
                .append("}\n");
        writeTo(mPath.toPath(), getServiceClassSourceName(className), packageName, sb.toString());
    }

    private void writeTo(Path directory, String className, String packageName, String content) throws IOException {
        Path outputDirectory = directory;
        if (!packageName.isEmpty()) {
            for (String packageComponent : packageName.split("\\.")) {
                outputDirectory = outputDirectory.resolve(packageComponent);
            }
            Files.createDirectories(outputDirectory);
        }

        Path outputPath = outputDirectory.resolve(className + ".java");
        Files.deleteIfExists(outputPath);
        Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath), UTF_8);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private String getInterfaceSourceName(String className) {
        return "I" + className;
    }

    private String getServiceClassSourceName(String className) {
        return className + "Service";
    }


    private void classifyMethodMap(Set<? extends Element> elements) {
        for (Element element : elements) {
            if (element.getKind() == ElementKind.METHOD && (element instanceof ExecutableElement)) {
                ExecutableElement executableElement = (ExecutableElement) element;
                Set<Modifier> modifiers = executableElement.getModifiers();
                //只能给共有的成员方法添加注解，否则生成的文件中没有相应的方法
                if (modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.STATIC)) {
                    String className = ClassName.get((TypeElement) element.getEnclosingElement()).simpleName();
                    String packageName = ClassName.get((TypeElement) element.getEnclosingElement()).packageName();
                    List<ExecutableElement> executableElements = methodMap.get(packageName + "." + className);
                    if (executableElements == null) {
                        executableElements = new ArrayList<>();
                        methodMap.put(packageName + "." + className, executableElements);
                    }
                    executableElements.add(executableElement);
                }
            }
        }
    }

    private Map<String, List<ClassName>> exportServiceMap = new HashMap<>();

    private void generateBuildResource() throws IOException {

        //先根据service注解中的moduleName是否为空进行分类
        Set<String> keySet = methodMap.keySet();
        for (String fullClassName : keySet) {
            List<ExecutableElement> elementList = methodMap.get(fullClassName);
            TypeElement typeElement = (TypeElement) elementList.get(0).getEnclosingElement();
            ExportService exportService = typeElement.getAnnotation(ExportService.class);
            ClassName typeElementClassName = ClassName.get(typeElement);
            if (exportService == null || "".equals(exportService.moduleName())) {
                List<ClassName> classNames = new ArrayList<>();
                classNames.add(typeElementClassName);
                exportServiceMap.put(typeElementClassName.packageName() + "." + typeElementClassName.simpleName(), classNames);
            } else {
                List<ClassName> classNames = exportServiceMap.get(exportService.moduleName());
                if (classNames == null) {
                    classNames = new ArrayList<>();
                }
                classNames.add(typeElementClassName);
                exportServiceMap.put(exportService.moduleName(), classNames);
            }
        }

        //生成serviceRegister类
        for (String key : exportServiceMap.keySet()) {
            List<ClassName> classNames = exportServiceMap.get(key);
            //registerService入参类型
            ParameterizedTypeName inputMapType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(Class.class)
            );
            //入参名称
            ParameterSpec paramSpec = ParameterSpec.builder(inputMapType, "servicesMap").build();
            //注册服务方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(REGISTER_METHOD_NAME)
                    .addModifiers(PUBLIC)
                    .addParameter(paramSpec);

//            for (String fullClassName : methodMap.keySet()) {
//                List<ExecutableElement> executableElements = methodMap.get(fullClassName);
//                String packageName = ClassName.get((TypeElement) executableElements.get(0).getEnclosingElement()).packageName();
//                methodBuilder.addStatement("servicesMap.put($S,$T.class)",
//                        packageName + "." + getInterfaceSourceName(getClassSimpleNameByFullName(fullClassName)),
//                        ClassName.get((TypeElement) executableElements.get(0).getEnclosingElement()));
//            }

            TypeElement iRegisterServiceElement = mElementUtils.getTypeElement(Constants.I_REGISTER_SERVICE);
            for (ClassName className : classNames) {
                methodBuilder.addStatement("servicesMap.put($S,$T.class)",
                        className.packageName() + "." + getInterfaceSourceName(getClassSimpleNameByFullName(className.packageName() + "." + className.simpleName())),
                        className);
            }

            JavaFile.builder(REGISTER_PACKAGE_NAME,
                    TypeSpec.classBuilder("AutoRegisterService_" + (key.replace(".", "_")))
//                    TypeSpec.classBuilder(key.replace(".", "_"))
                            .addJavadoc("Auto generate by apt, don't edit")
                            .addModifiers(PUBLIC)
                            .addSuperinterface(ClassName.get(iRegisterServiceElement))
                            .addMethod(methodBuilder.build())
                            .build()
            ).build().writeTo(mFiler);
        }

//        //registerService入参类型
//        ParameterizedTypeName inputMapType = ParameterizedTypeName.get(
//                ClassName.get(Map.class),
//                ClassName.get(String.class),
//                ClassName.get(Class.class)
//        );
//        //入参名称
//        ParameterSpec paramSpec = ParameterSpec.builder(inputMapType, "servicesMap").build();
//        //注册服务方法
//        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(REGISTER_METHOD_NAME)
//                .addModifiers(PUBLIC)
//                .addParameter(paramSpec);
//
//        for (String fullClassName : methodMap.keySet()) {
//            List<ExecutableElement> executableElements = methodMap.get(fullClassName);
//            String packageName = ClassName.get((TypeElement) executableElements.get(0).getEnclosingElement()).packageName();
//            methodBuilder.addStatement("servicesMap.put($S,$T.class)",
//                    packageName + "." + getInterfaceSourceName(getClassSimpleNameByFullName(fullClassName)),
//                    ClassName.get((TypeElement) executableElements.get(0).getEnclosingElement()));
//        }
//
//        TypeElement iRegisterServiceElement = mElementUtils.getTypeElement(Constants.I_REGISTER_SERVICE);

//        JavaFile.builder(REGISTER_PACKAGE_NAME,
//                TypeSpec.classBuilder("AutoRegisterService$$$" + moduleName)
//                        .addJavadoc("Auto generate by apt, don't edit")
//                        .addModifiers(PUBLIC)
//                        .addSuperinterface(ClassName.get(iRegisterServiceElement))
//                        .addMethod(methodBuilder.build())
//                        .build()
//        ).build().writeTo(mFiler);
    }

    private String getClassSimpleNameByFullName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());
    }
}
