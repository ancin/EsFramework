package org.es.framework.common.web.bind.method.annotation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.web.bind.annotation.SearchableDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.google.common.collect.Lists;

/**
 * �����ѯ�����ַ�������ҳ/��������󶨵�Searchable
 * <pre>
 *     ��ѯ������ʽ���£�
 *     1.1��Ĭ�ϲ�ѯ�ַ���
 *         search.baseInfo.realname_like=zhang
 *         search.age_lt=12
 *         ���򼰷�ҳ��ο� {@link PageableMethodArgumentResolver}
 *     1.2��������������д��
 *        public void test(Searchable searchable);
 *
 *     2.1���Զ����ѯ�ַ���
 *         foo_search.baseInfo.realname_like=zhang
 *         foo_search.age_lt=12
 *         test_search.age_gt=12
 *         ���򼰷�ҳ��ο� {@link PageableMethodArgumentResolver}
 *     2.2��������������д��
 *        public void test(@Qualifier("foo") Searchable searchable1, @Qualifier("test") Searchable searchable2);
 *
 *     3.1�����ò�ѯʱ��ҳ������
 *          public void test(@Search(page = false, sort = false) Searchable searchable);
 * </pre>
 * 
 */
public class SearchableMethodArgumentResolver extends BaseMethodArgumentResolver {

    private static final PageableMethodArgumentResolver DEFAULT_PAGEABLE_RESOLVER = new PageableMethodArgumentResolver();
    private static final String                         DEFAULT_SEARCH_PREFIX     = "search";

    private String                                      prefix                    = DEFAULT_SEARCH_PREFIX;

    /**
     * ���ò�ѯ����ǰ׺
     *
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * ��ҳ����������
     */
    private PageableMethodArgumentResolver pageableMethodArgumentResolver = DEFAULT_PAGEABLE_RESOLVER;

    public void setPageableMethodArgumentResolver(PageableMethodArgumentResolver pageableMethodArgumentResolver) {
        this.pageableMethodArgumentResolver = pageableMethodArgumentResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Searchable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
                                                                                                  throws Exception {

        String searchPrefix = getSearchPrefix(parameter);

        Map<String, String[]> searcheableMap = getPrefixParameterMap(searchPrefix, webRequest, true);

        boolean hasCustomSearchFilter = searcheableMap.size() > 0;

        SearchableDefaults searchDefaults = getSearchableDefaults(parameter);

        boolean needMergeDefault = searchDefaults != null && searchDefaults.merge();

        Searchable searchable = null;
        //�Զ��帲��Ĭ��
        if (needMergeDefault || !hasCustomSearchFilter) {
            searchable = getDefaultFromAnnotation(searchDefaults);
        }
        if (hasCustomSearchFilter) {
            if (searchable == null) {
                searchable = Searchable.newSearchable();
            }
            for (String name : searcheableMap.keySet()) {
                String[] mapValues = filterSearchValues(searcheableMap.get(name));

                if (mapValues.length == 1) {
                    if (name.endsWith("in")) {
                        searchable.addSearchParam(name, StringUtils.split(mapValues[0], ",; "));
                    } else {
                        searchable.addSearchParam(name, mapValues[0]);
                    }
                } else {
                    searchable.addSearchParam(name, mapValues);
                }
            }
        }

        Pageable pageable = (Pageable) pageableMethodArgumentResolver.resolveArgument(parameter,
            mavContainer, webRequest, binderFactory);
        //Ĭ�Ϸ�ҳ������
        if (searchDefaults == null) {
            searchable.setPage(pageable);
        }
        //needPage=true ��ҳ������
        if (searchDefaults != null && searchDefaults.needPage()) {
            searchable.setPage(pageable);
        }
        //needPage=false needSort=true  ��Ҫ��ҳ��������
        if (searchDefaults != null && !searchDefaults.needPage() && searchDefaults.needSort()) {
            searchable.addSort(pageable.getSort());
        }

        return searchable;
    }

    @SuppressWarnings("unchecked")
    private String[] filterSearchValues(String[] values) {
        List<String> result = Lists.newArrayList(CollectionUtils.arrayToList(values));
        for (int i = 0; i < result.size(); i++) {
            if (StringUtils.isBlank(result.get(i))) {
                result.remove(i);
            }
        }
        return result.toArray(values);
    }

    private String getSearchPrefix(MethodParameter parameter) {
        Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);

        if (qualifier != null) {
            return new StringBuilder(qualifier.value()).append("_").append(prefix).toString();
        }

        return prefix;
    }

    private SearchableDefaults getSearchableDefaults(MethodParameter parameter) {
        //���ȴӲ�������
        SearchableDefaults searchDefaults = parameter
            .getParameterAnnotation(SearchableDefaults.class);
        //�Ҳ����ӷ�������
        if (searchDefaults == null) {
            searchDefaults = parameter.getMethodAnnotation(SearchableDefaults.class);
        }
        return searchDefaults;
    }

    private Searchable getDefaultFromAnnotation(SearchableDefaults searchableDefaults) {

        Searchable searchable = defaultSearchable(searchableDefaults);
        if (searchable != null) {
            return searchable;
        }

        return Searchable.newSearchable();
    }

    private Searchable defaultSearchable(SearchableDefaults searchableDefaults) {

        if (searchableDefaults == null) {
            return null;
        }

        Searchable searchable = Searchable.newSearchable();
        for (String searchParam : searchableDefaults.value()) {
            String[] searchPair = searchParam.split("=");
            String paramName = searchPair[0];
            String paramValue = searchPair[1];
            if (paramName.endsWith("in")) {
                searchable.addSearchParam(paramName, StringUtils.split(paramValue, ",; "));
            } else {
                searchable.addSearchParam(paramName, paramValue);
            }
        }

        return searchable;
    }

}
