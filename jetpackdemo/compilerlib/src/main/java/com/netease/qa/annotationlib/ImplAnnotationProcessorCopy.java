package com.netease.qa.annotationlib;

import com.chrc.annotalib.ApiClass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author : chrc
 * date   : 10/13/21  10:38 AM
 * desc   :
 */
@AutoService(Processor.class)
public class ImplAnnotationProcessorCopy extends AbstractProcessor {
    private Messager annotationLog;
    private Elements elementsUitls;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationLog = processingEnv.getMessager();
        elementsUitls = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        System.out.println("注解处理器运行.......................init................");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println("注解处理器运行.......................process befor................");
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        informMsg("注解处理器运行.......................................");
        System.out.println("注解处理器运行...............process ing........................");

        HashMap<String, ImplAnnotationInfo> implMap = new HashMap<>();

        for (Element implElement : roundEnv.getElementsAnnotatedWith(ApiClass.class)) {

            if (implElement.getKind() != ElementKind.CLASS) {
                informError(implElement, "@Impl 只能注解在类上！注解失效！");
                continue;
            }

            ImplAnnotationInfo implAnnotationInfo = getImplAnnotationInfo((TypeElement) implElement);
            System.out.println("注解处理器运行...............process ing name="+implAnnotationInfo.name+"_"+implAnnotationInfo.implClass.simpleName()+
                    "_"+implAnnotationInfo.implClass.packageName()+"........................");

            if (implAnnotationInfo == null) continue;

            implMap.put(implAnnotationInfo.name, implAnnotationInfo);
        }

        new ImplClassProtocolGenerate(elementsUitls, filer).generateImplProtocolClass(implMap);

        informMsg("注解处理器运行结束.......................................");

        return true;
    }

    //implClassElement 为被注解的类
    private ImplAnnotationInfo getImplAnnotationInfo(TypeElement implClassElement) {
        ApiClass implAnnotation = implClassElement.getAnnotation(ApiClass.class);
        ClassName implClassName = ClassName.get(implClassElement);
        String implName = implAnnotation.description();
        return new ImplAnnotationInfo(implName, implClassName);
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        System.out.println("注解处理器运行.......................getSupportedAnnotationTypes................");
        HashSet<String> set = new HashSet<>();
        set.add(ApiClass.class.getCanonicalName());
        return set;
    }


    private void informError(Element e, String msg, Object... args) {
//        annotationLog.printMessage(
//                Diagnostic.Kind.ERROR,
//                String.format(msg, args),
//                e);
    }

    private void informMsg(String msg, Object... args) {
//        annotationLog.printMessage(
//                Diagnostic.Kind.NOTE,
//                String.format(msg, args));
    }

}
