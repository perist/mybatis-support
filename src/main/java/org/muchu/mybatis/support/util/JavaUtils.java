package org.muchu.mybatis.support.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.muchu.mybatis.support.constant.MyBatisTag;

import java.util.Objects;

/**
 * @author heber
 */
public class JavaUtils {

    public static PsiNameIdentifierOwner process(PsiElement element) {
        if (element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            if (Objects.equals(MyBatisTag.MAPPER.getValue(), xmlTag.getName())) {
                return processMapper(xmlTag);
            } else if (MyBatisTag.isCRUDStatement(xmlTag.getName())) {
                return processCRUDStatement(xmlTag);
            }
        }
        return null;
    }

    private static PsiClass processMapper(XmlTag xmlTag) {
        XmlAttribute namespace = xmlTag.getAttribute("namespace");
        if (namespace != null && StringUtils.isNotBlank(namespace.getValue())) {
            Project project = xmlTag.getProject();
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            return javaPsiFacade.findClass(namespace.getValue(), GlobalSearchScope.allScope(project));
        }
        return null;
    }

    private static PsiMethod processCRUDStatement(XmlTag xmlTag) {
        XmlTag parentTag = xmlTag.getParentTag();
        XmlAttribute id = xmlTag.getAttribute(MyBatisTag.ID.getValue());
        if (parentTag != null && id != null && StringUtils.isNotBlank(id.getValue())) {
            PsiClass psiClass = processMapper(parentTag);
            if (psiClass != null) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    if (Objects.equals(method.getName(), id.getValue())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
}
