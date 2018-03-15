package org.es.framework.common.service;

import java.io.Serializable;
import java.util.List;

import org.es.framework.common.entity.AbstractEntity;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Lists;

/***
 *  <p>����service����� �ṩһЩ��㷽��<p/>
 * 
 * <p>���� �� M ��ʾʵ�����ͣ�ID��ʾ��������<p/>
 * 
 * @author kejun.song
 * @version $Id: BaseService.java, v 0.1
 */
@SuppressWarnings("rawtypes")
public abstract class BaseService<M extends AbstractEntity, ID extends Serializable> {

    @Autowired
    protected BaseRepository<M, ID> baseRepository;

    @Autowired
    public void setBaseRepository(BaseRepository<M, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    /**
     * ���浥��ʵ��
     *
     * @param m ʵ��
     * @return ���ر����ʵ��
     */
    public M save(M m) {
        return baseRepository.save(m);
    }

    public M saveAndFlush(M m) {
        m = save(m);
        baseRepository.flush();
        return m;
    }

    /**
     * ���µ���ʵ��
     *
     * @param m ʵ��
     * @return ���ظ��µ�ʵ��
     */
    public M update(M m) {
        return baseRepository.save(m);
    }

    /**
     * ��������ɾ����Ӧʵ��
     *
     * @param id ����
     */
    public void delete(ID id) {
        baseRepository.delete(id);
    }

    /**
     * ɾ��ʵ��
     *
     * @param m ʵ��
     */
    public void delete(M m) {
        baseRepository.delete(m);
    }

    /**
     * ��������ɾ����Ӧʵ��
     *
     * @param ids ʵ��
     */
    public void delete(ID[] ids) {
        baseRepository.delete(ids);
    }

    /**
     * ����������ѯ
     *
     * @param id ����
     * @return ����id��Ӧ��ʵ��
     */
    public M findOne(ID id) {
        return baseRepository.findOne(id);
    }

    /**
     * ʵ���Ƿ����
     *
     * @param id ����
     * @return ���� ����true������false
     */
    public boolean exists(ID id) {
        return baseRepository.exists(id);
    }

    /**
     * ͳ��ʵ������
     *
     * @return ʵ������
     */
    public long count() {
        return baseRepository.count();
    }

    /**
     * ��ѯ����ʵ��
     *
     * @return
     */
    public List<M> findAll() {
        return baseRepository.findAll();
    }

    /**
     * ����˳���ѯ����ʵ��
     *
     * @param sort
     * @return
     */
    public List<M> findAll(Sort sort) {
        return baseRepository.findAll(sort);
    }

    /**
     * ��ҳ�������ѯʵ��
     *
     * @param pageable ��ҳ����������
     * @return
     */
    public Page<M> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    /**
     * ��������ҳ�������ѯʵ��
     *
     * @param searchable ����
     * @return
     */
    public Page<M> findAll(Searchable searchable) {
        return baseRepository.findAll(searchable);
    }

    /**
     * ����������ҳ�������ѯʵ��
     *
     * @param searchable ����
     * @return
     */
    public List<M> findAllWithNoPageNoSort(Searchable searchable) {
        searchable.removePageable();
        searchable.removeSort();
        return Lists.newArrayList(baseRepository.findAll(searchable).getContent());
    }

    /**
     * �����������ѯʵ��(����ҳ)
     *
     * @param searchable ����
     * @return
     */
    public List<M> findAllWithSort(Searchable searchable) {
        searchable.removePageable();
        return Lists.newArrayList(baseRepository.findAll(searchable).getContent());
    }

    /**
     * ��������ҳ������ͳ��ʵ������
     *
     * @param searchable ����
     * @return
     */
    public Long count(Searchable searchable) {
        return baseRepository.count(searchable);
    }

}
