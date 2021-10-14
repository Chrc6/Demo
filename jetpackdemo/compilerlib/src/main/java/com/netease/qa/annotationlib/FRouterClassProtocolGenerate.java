package com.netease.qa.annotationlib;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by susion on 2018/10/27.
 * 生成 ImplInfo_xxxx.java
 */
public class FRouterClassProtocolGenerate {

    private Elements elementsUtils;
    private Filer filer;

    FRouterClassProtocolGenerate(Elements elements, Filer filer) {
        elementsUtils = elements;
        this.filer = filer;
    }

    void generateImplProtocolClass(List<FRouterAnnotationInfo> fRouterAnnotationInfos) {
        if (fRouterAnnotationInfos == null || fRouterAnnotationInfos.size() == 0) return;
        createJavaFile(fRouterAnnotationInfos);
    }

    private void createJavaFile(List<FRouterAnnotationInfo> fRouterAnnotationInfosp) {
        try {
            //创建一个新的源文件，并返回一个对象以允许写入它
            JavaFileObject jfo = filer.createSourceFile(
                    ProtocolConstants.FROUTER_PROTOCOL_GEN_PKG,
                    typeElement(""));
            Writer writer = jfo.openWriter();
            writer.write(generateJavaCode(fRouterAnnotationInfosp));
            writer.flush();
            writer.close();
        } catch (IOException e) {
//            error(proxyInfo.getTypeElement(),
//                    "Unable to write injector for type %s: %s",
//                    proxyInfo.getTypeElement(), e.getMessage());
        }
    }

    /**
     * 生成代码
     *
     * @return
     */
    public String generateJavaCode(List<FRouterAnnotationInfo> fRouterAnnotationInfos) {
        StringBuilder builder = new StringBuilder();
//        builder.append("// Generated code. Do not modify!\n");

        for (FRouterAnnotationInfo fRouterAnnotationInfo : fRouterAnnotationInfos) {
            builder.append("//").append(fRouterAnnotationInfo.className).append(":").append(fRouterAnnotationInfo.classValue).append("\n");
        }
        return builder.toString();
    }

    private void writeImplProtocolCode(TypeSpec code) {
        try {
            //每次编译都会生成一个文件。  FIXME:上次编译的文件应该删除
            JavaFile.builder(ProtocolConstants.FROUTER_PROTOCOL_GEN_PKG, code)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CodeBlock getImplProtocolInitCode(ImplAnnotationInfo implAnnotationInfo) {
        CodeBlock.Builder codeBuild = CodeBlock.builder();
        ClassName implLoader = className(ProtocolConstants.IMPL_LOADER_CLASS);
        if (implLoader == null) return codeBuild.build();
        codeBuild.addStatement("$T." + ProtocolConstants.IMPL_LOADER_REGISTER_IMPL_METHOD + "($S, $T.class)", implLoader, implAnnotationInfo.name, implAnnotationInfo.implClass);
        return codeBuild.build();
    }

    private MethodSpec.Builder getImplProtocolMethodSpec() {
        return MethodSpec.methodBuilder(ProtocolConstants.IMPL_LOADER_HELP_INIT_METHOD).addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(TypeName.VOID);
    }

    private TypeSpec.Builder getImplProtocolSpec() {
//        return TypeSpec.classBuilder(ProtocolConstants.IMPL_INFO_CLASS_PREFIX + "_" + UUID.randomUUID().toString().replace('-', '_'))
        return TypeSpec.classBuilder(ProtocolConstants.IMPL_INFO_CLASS_PREFIX + "_navInfo")
                .addSuperinterface(ClassName.get(RegisterImplLoaderInfo.class))
                .addModifiers(Modifier.PUBLIC);
    }

    /**
     * 从字符串获取ClassName对象
     */
    public ClassName className(String className) {
        TypeElement element = typeElement(className);
        if (element == null) return null;
        return ClassName.get(element);
    }

    /**
     * 从字符串获取TypeElement对象
     */
    public TypeElement typeElement(String className) {
        return elementsUtils.getTypeElement(className);
    }


}