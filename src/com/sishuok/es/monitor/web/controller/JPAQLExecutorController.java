package com.sishuok.es.monitor.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.es.framework.common.Constants;
import org.es.framework.common.repository.hibernate.HibernateUtils;
import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.es.framework.common.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping("/admin/monitor/db")
@RequiresPermissions("monitor:ql:*")
public class JPAQLExecutorController extends BaseController {

    @PersistenceContext
    private EntityManager              em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @RequestMapping(value = "/ql", method = RequestMethod.GET)
    public String showQLForm() {
        return viewName("qlForm");
    }

    @PageableDefaults(pageNumber = 0, value = 10)
    @RequestMapping(value = "/ql", method = RequestMethod.POST)
    public String executeQL(final @RequestParam("ql") String ql, final Model model,
                            final Pageable pageable) {

        model.addAttribute("sessionFactory", HibernateUtils.getSessionFactory(em));

        try {
            new TransactionTemplate(transactionManager).execute(new TransactionCallback<Void>() {
                @SuppressWarnings("unchecked")
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    //1���ȳ���ִ��ql����
                    try {
                        Query query = em.createQuery(ql);
                        int updateCount = query.executeUpdate();
                        model.addAttribute("updateCount", updateCount);
                        return null;
                    } catch (Exception e) {
                    }
                    //2���ʧ�� ����ִ��ql��ѯ
                    String findQL = ql;
                    String alias = QueryUtils.detectAlias(findQL);
                    if (StringUtils.isEmpty(alias)) {
                        Pattern pattern = Pattern.compile("^(.*\\s*from\\s+)(.*)(\\s*.*)",
                            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
                        findQL = pattern.matcher(findQL).replaceFirst("$1$2 o $3");
                    }
                    String countQL = QueryUtils.createCountQueryFor(findQL);
                    Query countQuery = em.createQuery(countQL);
                    Query findQuery = em.createQuery(findQL);
                    findQuery.setFirstResult(pageable.getOffset());
                    findQuery.setMaxResults(pageable.getPageSize());

                    Page page = new PageImpl(findQuery.getResultList(), pageable, (Long) countQuery
                        .getSingleResult());

                    model.addAttribute("resultPage", page);
                    return null;
                }
            });
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.addAttribute(Constants.ERROR, sw.toString());
        }

        return showQLForm();
    }

}
