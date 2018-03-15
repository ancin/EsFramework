package org.es.framework.common.web.bind.method.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.google.common.collect.Lists;

/**
 * <p>�����ҳ���ݰ󶨵�Pageable��֧�����������uri template���ݵİ�</p>
 * <p>ʹ��ָ�ϣ�
 * <pre>
 *   1.1���򵥵ķ�ҳ���������ʽ���£�
 *     page.size=10  ��ҳ��С
 *     page.pn=1    ҳ�� ��1��ʼ
 *   1.2��������������д��
 *     public void test(Pageable page);
 * </pre>
 * <pre>
 *   2.1��������ķ�ҳ���������ʽ���£�
 *     page.size=10  ��ҳ��С
 *     page.pn=1    ҳ�� ��1��ʼ
 *     sort.a.b=desc
 *     sort.c=asc
 *   Ĭ�ϰ�������ؼ��ʵ��ֵ�˳���ţ���ΪMap�洢������ ����������� ������sort֮���һ��˳���
 *     sort2.a.b=desc
 *     sort1.c=asc
 *   2.2��������������д��
 *     public void test(Pageable page);
 * </pre>
 * <pre>
 *   3.1����ǰ׺�� �����ҳ���������ʽ���£�
 *     test_page.size=10  ��ҳ��С
 *     test_page.pn=1    ҳ�� ��1��ʼ
 *     test_sort.a.b=desc
 *     test_sort.c=asc
 *     foo_page.size=10  ��ҳ��С
 *     foo_page.pn=1    ҳ�� ��1��ʼ
 *     foo_sort.a.b=desc
 *     foo_sort.c=asc
 *
 *   ����Ĭ�ϰ�������ʱ˳����
 *   3.2��������������д��
 *     public void test(@Qualifier("test") Pageable page1, @Qualifier("test") Pageable page2);
 * </pre>
 * <p/>
 * <pre>
 *     ������÷�������ж����ʹ��@Qualifierָ��ǰ׺
 *     public void fail(Pageable page1, Pageable page2);
 * </pre>
 * <p/>
 * 
 */
public class PageableMethodArgumentResolver extends BaseMethodArgumentResolver {

    private static final Pageable DEFAULT_PAGE_REQUEST = new PageRequest(0, 10);
    private static final String   DEFAULT_PAGE_PREFIX  = "page";
    private static final String   DEFAULT_SORT_PREFIX  = "sort";

    private Pageable              fallbackPagable      = DEFAULT_PAGE_REQUEST;
    private String                pagePrefix           = DEFAULT_PAGE_PREFIX;
    private String                sortPrefix           = DEFAULT_SORT_PREFIX;

    private int                   minPageSize          = 5;
    private int                   maxPageSize          = 100;

    /**
     * ������С��ҳ��С Ĭ��10
     *
     * @param minPageSize
     */
    public void setMinPageSize(int minPageSize) {
        this.minPageSize = minPageSize;
    }

    /**
     * ��������ҳ��С Ĭ��100
     *
     * @param maxPageSize
     */
    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    /**
     * Setter to configure a fallback instance of {@link Pageable} that is being used to back missing parameters. Defaults
     * to {@value #DEFAULT_PAGE_REQUEST}.
     *
     * @param fallbackPagable the fallbackPagable to set
     */
    public void setFallbackPagable(Pageable fallbackPagable) {
        this.fallbackPagable = null == fallbackPagable ? DEFAULT_PAGE_REQUEST : fallbackPagable;
    }

    /**
     * Setter to configure the prefix of request parameters to be used to retrieve paging information. Defaults to
     * {@link #DEFAULT_PAGE_PREFIX}.
     *
     * @param pagePrefix the prefix to set
     */
    public void setPagePrefix(String pagePrefix) {
        this.pagePrefix = null == pagePrefix ? DEFAULT_PAGE_PREFIX : pagePrefix;
    }

    public void setSortPrefix(String sortPrefix) {
        this.sortPrefix = null == sortPrefix ? DEFAULT_SORT_PREFIX : sortPrefix;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
                                                                                                  throws Exception {

        PageableDefaults pageableDefaults = getPageableDefaults(parameter);
        //Ĭ�ϵ�page request
        Pageable defaultPageRequest = getDefaultFromAnnotationOrFallback(pageableDefaults);

        String pageableNamePrefix = getPagePrefix(parameter);
        String sortNamePrefix = getSortPrefix(parameter);
        Map<String, String[]> pageableMap = getPrefixParameterMap(pageableNamePrefix, webRequest,
            true);
        Map<String, String[]> sortMap = getPrefixParameterMap(sortNamePrefix, webRequest, false);

        Sort sort = getSort(sortNamePrefix, sortMap, defaultPageRequest, webRequest);
        if (pageableMap.size() == 0) {
            return new PageRequest(defaultPageRequest.getPageNumber(),
                defaultPageRequest.getPageSize(), sort == null ? defaultPageRequest.getSort()
                    : sort);
        }

        int pn = getPn(pageableMap, defaultPageRequest);
        int pageSize = getPageSize(pageableMap, defaultPageRequest);

        return new PageRequest(pn - 1, pageSize, sort);

    }

    private Sort getSort(String sortNamePrefix, Map<String, String[]> sortMap,
                         Pageable defaultPageRequest, NativeWebRequest webRequest) {
        Sort sort = null;
        List<OrderedSort> orderedSortList = Lists.newArrayList();
        for (String name : sortMap.keySet()) {

            //sort1.abc
            int propertyIndex = name.indexOf(".") + 1;

            int order = 0;
            String orderStr = name.substring(sortNamePrefix.length(), propertyIndex - 1);
            try {
                if (!StringUtils.isEmpty(orderStr)) {
                    order = Integer.valueOf(orderStr);
                }
            } catch (Exception e) {
            }

            String property = name.substring(propertyIndex);
            assertSortProperty(property);
            Sort.Direction direction = Sort.Direction.fromString(sortMap.get(name)[0]);

            orderedSortList.add(new OrderedSort(property, direction, order));
        }

        Collections.sort(orderedSortList);
        for (OrderedSort orderedSort : orderedSortList) {
            Sort newSort = new Sort(orderedSort.direction, orderedSort.property);
            if (sort == null) {
                sort = newSort;
            } else {
                sort = sort.and(newSort);
            }
        }

        if (sort == null) {
            return defaultPageRequest.getSort();
        }

        return sort;
    }

    /**
     * ��ֹsqlע�룬�����ַ���ֻ�ܰ����ַ� ���� �»��� �� ` "
     *
     * @param property
     */
    private void assertSortProperty(String property) {
        if (!property.matches("[a-zA-Z0-9_��.`\"]*")) {
            throw new IllegalStateException("Sort property error, only contains [a-zA-Z0-9_.`\"]");
        }
    }

    private int getPageSize(Map<String, String[]> pageableMap, Pageable defaultPageRequest) {
        int pageSize = 0;
        try {
            String pageSizeStr = pageableMap.get("size")[0];
            if (pageSizeStr != null) {
                pageSize = Integer.valueOf(pageSizeStr);
            } else {
                pageSize = defaultPageRequest.getPageSize();
            }
        } catch (Exception e) {
            pageSize = defaultPageRequest.getPageSize();
        }

        if (pageSize < minPageSize) {
            pageSize = minPageSize;
        }

        if (pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }
        return pageSize;
    }

    private int getPn(Map<String, String[]> pageableMap, Pageable defaultPageRequest) {
        int pn = 1;
        try {
            String pnStr = pageableMap.get("pn")[0];
            if (pnStr != null) {
                pn = Integer.valueOf(pnStr);
            } else {
                pn = defaultPageRequest.getPageNumber();
            }
        } catch (Exception e) {
            pn = defaultPageRequest.getPageNumber();
        }

        if (pn < 1) {
            pn = 1;
        }

        return pn;
    }

    /**
     * Resolves the prefix to use to bind properties from. Will prepend a possible {@link Qualifier} if available or
     * return the configured prefix otherwise.
     *
     * @param parameter
     * @return
     */
    private String getPagePrefix(MethodParameter parameter) {

        Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);

        if (qualifier != null) {
            return new StringBuilder(qualifier.value()).append("_").append(pagePrefix).toString();
        }

        return pagePrefix;
    }

    private String getSortPrefix(MethodParameter parameter) {

        Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);

        if (qualifier != null) {
            return new StringBuilder(qualifier.value()).append("_").append(sortPrefix).toString();
        }

        return sortPrefix;
    }

    private Pageable getDefaultFromAnnotationOrFallback(PageableDefaults pageableDefaults) {

        Pageable defaultPageable = defaultPageable(pageableDefaults);
        if (defaultPageable != null) {
            return defaultPageable;
        }

        return fallbackPagable;
    }

    private PageableDefaults getPageableDefaults(MethodParameter parameter) {
        //���ȴӲ�������
        PageableDefaults pageableDefaults = parameter
            .getParameterAnnotation(PageableDefaults.class);
        //�Ҳ����ӷ�������
        if (pageableDefaults == null) {
            pageableDefaults = parameter.getMethodAnnotation(PageableDefaults.class);
        }
        return pageableDefaults;
    }

    private Pageable defaultPageable(PageableDefaults pageableDefaults) {

        if (pageableDefaults == null) {
            return null;
        }

        int pageNumber = pageableDefaults.pageNumber();
        int pageSize = pageableDefaults.value();

        String[] sortStrArray = pageableDefaults.sort();
        Sort sort = null;

        for (String sortStr : sortStrArray) {
            String[] sortStrPair = sortStr.split("=");
            Sort newSort = new Sort(Sort.Direction.fromString(sortStrPair[1]), sortStrPair[0]);
            if (sort == null) {
                sort = newSort;
            } else {
                sort = sort.and(newSort);
            }
        }
        return new PageRequest(pageNumber, pageSize, sort);
    }

    /**
     * Asserts uniqueness of all {@link Pageable} parameters of the method of the given {@link MethodParameter}.
     *
     * @param parameter
     */
    @SuppressWarnings("unused")
    private void assertPageableUniqueness(MethodParameter parameter) {

        Method method = parameter.getMethod();

        if (containsMoreThanOnePageableParameter(method)) {
            Annotation[][] annotations = method.getParameterAnnotations();
            assertQualifiersFor(method.getParameterTypes(), annotations);
        }
    }

    /**
     * Returns whether the given {@link Method} has more than one {@link Pageable} parameter.
     *
     * @param method
     * @return
     */
    private boolean containsMoreThanOnePageableParameter(Method method) {

        boolean pageableFound = false;

        for (Class<?> type : method.getParameterTypes()) {

            if (pageableFound && type.equals(Pageable.class)) {
                return true;
            }

            if (type.equals(Pageable.class)) {
                pageableFound = true;
            }
        }

        return false;
    }

    /**
     * Asserts that every {@link Pageable} parameter of the given parameters carries an {@link org.springframework.beans.factory.annotation.Qualifier} annotation to
     * distinguish them from each other.
     *
     * @param parameterTypes
     * @param annotations
     */
    private void assertQualifiersFor(Class<?>[] parameterTypes, Annotation[][] annotations) {

        Set<String> values = new HashSet<String>();

        for (int i = 0; i < annotations.length; i++) {

            if (Pageable.class.equals(parameterTypes[i])) {

                Qualifier qualifier = findAnnotation(annotations[i]);

                if (null == qualifier) {
                    throw new IllegalStateException(
                        "Ambiguous Pageable arguments in handler method. If you use multiple parameters of type Pageable you need to qualify them with @Qualifier");
                }

                if (values.contains(qualifier.value())) {
                    throw new IllegalStateException("Values of the user Qualifiers must be unique!");
                }

                values.add(qualifier.value());
            }
        }
    }

    /**
     * Returns a {@link Qualifier} annotation from the given array of {@link Annotation}s. Returns {@literal null} if the
     * array does not contain a {@link Qualifier} annotation.
     *
     * @param annotations
     * @return
     */
    private Qualifier findAnnotation(Annotation[] annotations) {

        for (Annotation annotation : annotations) {
            if (annotation instanceof Qualifier) {
                return (Qualifier) annotation;
            }
        }

        return null;
    }

    static class OrderedSort implements Comparable<OrderedSort> {
        private final String         property;
        private final Sort.Direction direction;
        private int                  order = 0; //Ĭ��0 ������

        OrderedSort(String property, Sort.Direction direction, int order) {
            this.property = property;
            this.direction = direction;
            this.order = order;
        }

        @Override
        public int compareTo(OrderedSort o) {
            if (o == null) {
                return -1;
            }
            if (this.order > o.order) {
                return 1;
            } else if (this.order < o.order) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
