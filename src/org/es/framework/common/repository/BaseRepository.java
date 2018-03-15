package org.es.framework.common.repository;

import java.io.Serializable;
import java.util.List;

import org.es.framework.common.entity.search.Searchable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>����DAO����� �ṩһЩ��㷽��<br/>
 * ����ʹ����ο�����������{@see com.sishuok.es.common.repository.UserRepository}
 * <p/>
 * ��Ҫʹ�øýӿ���Ҫ��spring�����ļ���jpa:repositories�����
 * factory-class="com.sishuok.es.common.repository.support.SimpleBaseRepositoryFactoryBean"
 * <p/>
 * <p>���� �� M ��ʾʵ�����ͣ�ID��ʾ��������
 * 
 */
@NoRepositoryBean
public interface BaseRepository<M, ID extends Serializable> extends JpaRepository<M, ID> {

    /**
     * ��������ɾ��
     *
     * @param ids
     */
    public void delete(ID[] ids);

    /*
    * (non-Javadoc)
    * @see org.springframework.data.repository.CrudRepository#findAll()
    */
    @Override
    List<M> findAll();

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    List<M> findAll(Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    @Override
    Page<M> findAll(Pageable pageable);

    /**
     * ����������ѯ����
     * ���� + ��ҳ + ����
     *
     * @param searchable
     * @return
     */
    public Page<M> findAll(Searchable searchable);

    /**
     * ��������ͳ�����м�¼��
     *
     * @param searchable
     * @return
     */
    public long count(Searchable searchable);

}
