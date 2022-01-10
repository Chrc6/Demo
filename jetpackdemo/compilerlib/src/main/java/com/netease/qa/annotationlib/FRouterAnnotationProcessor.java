package com.netease.qa.annotationlib;

import com.chrc.annotalib.FRouterClass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.tools.Diagnostic;

/**
 * @author : chrc
 * date   : 10/13/21  10:38 AM
 * desc   :
 * https://www.imooc.com/article/280634
 * https://www.jianshu.com/p/3052fa51ee95
 * https://www.jianshu.com/p/8dc7a49d86be
 * https://blog.csdn.net/kaifa1321/article/details/79683246
 */
@AutoService(Processor.class)
public class FRouterAnnotationProcessor extends AbstractProcessor {
    private Messager annotationLog;
    private Elements elementsUitls;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationLog = processingEnv.getMessager();
        elementsUitls = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("注解处理器运行.......................process befor................");
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        informMsg("注解处理器运行.......................................");
        List<FRouterAnnotationInfo> fRouterAnnotationInfos = new ArrayList<>();

        for (Element implElement : roundEnv.getElementsAnnotatedWith(FRouterClass.class)) {
            if (implElement.getKind() != ElementKind.CLASS) {
                informError(implElement, "@Impl 只能注解在类上！注解失效！");
                continue;
            }
            FRouterAnnotationInfo annotationInfo = getImplAnnotationInfo((TypeElement) implElement);
            System.out.println("注解处理器运行...............process ing name=" + annotationInfo.className + "_" + annotationInfo.classValue);
            if (annotationInfo == null) continue;
            fRouterAnnotationInfos.add(annotationInfo);
        }
        new FRouterClassProtocolGenerate(elementsUitls, filer).generateImplProtocolClass(fRouterAnnotationInfos);
        informMsg("注解处理器运行结束.......................................");
        return true;
    }

    //implClassElement 为被注解的类
    private FRouterAnnotationInfo getImplAnnotationInfo(TypeElement implClassElement) {
        FRouterClass implAnnotation = implClassElement.getAnnotation(FRouterClass.class);
        ClassName implClassName = ClassName.get(implClassElement);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
//        sb.append("action_id").append(":").append(implAnnotation.action_id()).append(",");
//        sb.append("action_destination").append(":").append(implAnnotation.action_destination()).append(",");
//        sb.append("fragment_id").append(":").append(implAnnotation.fragment_id()).append(",");
//        if (!implAnnotation.fragment_name().isEmpty()) {
//            sb.append("fragment_name").append(":").append(implAnnotation.fragment_name()).append(",");
//        } else {
//            sb.append("fragment_name").append(":").append(implClassName.packageName() + "." + implClassName.simpleName()).append(",");
//        }
//        sb.append("fragment_arguments_name").append(":").append(implAnnotation.fragment_arguments_name()).append(",");
//        sb.append("fragment_arguments_argType").append(":").append(implAnnotation.fragment_arguments_argType()).append(",");
//        sb.append("fragment_arguments_defaultValue").append(":").append(implAnnotation.fragment_arguments_defaultValue());

        System.out.println("---------------startDestination="+implAnnotation.startDestination()+"---------------------");
        addKeyAndValue(sb, "startDestination", implAnnotation.startDestination(), false);
        addKeyAndValue(sb, "action_id", implAnnotation.action_id(), false);
        addKeyAndValue(sb, "action_destination", implAnnotation.action_destination(), false);
        addKeyAndValue(sb, "fragment_id", implAnnotation.fragment_id(), false);
        addKeyAndValue(sb, "fragment_name", implAnnotation.fragment_name().isEmpty() ? implClassName.packageName() + "." + implClassName.simpleName()
                :implAnnotation.fragment_name(), false);
        addKeyAndValue(sb, "fragment_arguments_name", implAnnotation.fragment_arguments_name(), false);
        addKeyAndValue(sb, "fragment_arguments_argType", implAnnotation.fragment_arguments_argType(), false);
        addKeyAndValue(sb, "fragment_arguments_defaultValue", implAnnotation.fragment_arguments_defaultValue(), true);
        sb.append("]").append("\n");
        return new FRouterAnnotationInfo(implClassName.simpleName(), sb.toString());
    }

    private StringBuilder addKeyAndValue(StringBuilder sb, String key, String value, boolean isLast) {
        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
            sb.append(key).append(":").append(value);
            if (!isLast) {
                sb.append(",");
            }
        }
        return sb;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(FRouterClass.class.getCanonicalName());
        return set;
    }
    private void informError(Element e, String msg, Object... args) {
        annotationLog.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
    private void informMsg(String msg, Object... args) {
        annotationLog.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }
}
