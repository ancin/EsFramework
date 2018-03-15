package org.es.framework.common.plugin.serivce;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.plugin.entity.Movable;
import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.service.BaseService;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Maps;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: BaseMovableService.java, v 0.1 2014��11��19�� ����10:51:28 kejun.song Exp $
 */
@SuppressWarnings("rawtypes")
public abstract class BaseMovableService<M extends BaseEntity & Movable, ID extends Serializable>
                                                                                                  extends
                                                                                                  BaseService<M, ID> {

    //Ȩ�صĲ���
    private final Integer stepLength;

    /**
     * Ĭ�ϲ���1000
     */
    protected BaseMovableService() {
        this(1000);

    }

    protected BaseMovableService(Integer stepLength) {
        this.stepLength = stepLength;
    }

    /**
     * Ȩ�صĲ��� Ĭ��1000
     *
     * @return
     */
    public Integer getStepLength() {
        return stepLength;
    }

    @Override
    public M save(M m) {
        if (m.getWeight() == null) {
            m.setWeight(findNextWeight());
        }
        return super.save(m);
    }

    /**
     * ���ս�������ƶ�
     * ��{fromId}�ƶ���{}toId}֮��
     * �� fromWeight 2000 toWeight 1000   ���µ�Ϊ 500
     *
     * @param fromId
     * @param toId
     */
    public void down(ID fromId, ID toId) {
        M from = findOne(fromId);
        M to = findOne(toId);
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();

        M nextTo = findNextByWeight(toWeight);

        //���toId����һ����fromId ��ֱ�ӽ���˳�򼴿�
        if (from.equals(nextTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }

        int minWeight = Math.min(fromWeight, toWeight);
        int maxWeight = Math.max(fromWeight, toWeight);
        //��Ϊһ���Ż������ٲ�������weight
        int count = countByBetween(minWeight, maxWeight).intValue();
        if (count > 0 && count < 20) {
            List<M> moves = findByBetweenAndAsc(minWeight, maxWeight);
            if (fromWeight < toWeight) {
                Integer swapInteger = moves.get(count - 2).getWeight();
                for (int i = count - 2; i >= 1; i--) {
                    //���һ����weight = toWeight;
                    moves.get(i).setWeight(moves.get(i - 1).getWeight());
                }
                moves.get(0).setWeight(swapInteger);
            } else {
                for (int i = 0; i <= count - 2; i++) {
                    moves.get(i).setWeight(moves.get(i + 1).getWeight());
                }
                moves.get(count - 1).setWeight(minWeight);
            }
            return;
        }

        M preTo = findPreByWeight(toWeight);

        //�����µ�Ȩ��
        int newWeight = 0;
        if (preTo == null) {
            newWeight = toWeight / 2;
        } else {
            newWeight = toWeight - (toWeight - preTo.getWeight()) / 2;

        }

        if (Math.abs(newWeight - toWeight) <= 1) {
            throw new IllegalStateException(String.format(
                "up error, no enough weight :fromId:%d, toId:%d", fromId, toId));
        }
        from.setWeight(newWeight);

    }

    /**
     * ���ս�������ƶ�
     * ��{fromId}�ƶ���toId֮��
     * �� fromWeight 1000 toWeight 2000  3000 ���µ�Ϊ 2500
     *
     * @param fromId
     * @param toId
     */
    public void up(ID fromId, ID toId) {
        M from = findOne(fromId);
        M to = findOne(toId);
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();

        M preTo = findPreByWeight(toWeight);
        //���toId����һ����fromId ��ֱ�ӽ���˳�򼴿�
        if (from.equals(preTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }

        int minWeight = Math.min(fromWeight, toWeight);
        int maxWeight = Math.max(fromWeight, toWeight);
        //��Ϊһ���Ż������ٲ�������weight
        int count = countByBetween(minWeight, maxWeight).intValue();
        if (count > 0 && count < 20) {
            List<M> moves = findByBetweenAndDesc(minWeight, maxWeight);
            //123/124
            //5000 4000 3000

            if (fromWeight > toWeight) {
                Integer swapInteger = moves.get(count - 2).getWeight();
                for (int i = count - 2; i >= 1; i--) {
                    //���һ����weight = toWeight;
                    moves.get(i).setWeight(moves.get(i - 1).getWeight());
                }
                moves.get(0).setWeight(swapInteger);
            } else {
                for (int i = 0; i <= count - 2; i++) {
                    moves.get(i).setWeight(moves.get(i + 1).getWeight());
                }
                moves.get(count - 1).setWeight(maxWeight);
            }
            return;
        }

        //���toId����һ����fromId ��ֱ�ӽ���˳�򼴿�
        if (from.equals(preTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }
        M nextTo = findNextByWeight(toWeight);

        //�����µ�Ȩ��
        int newWeight = 0;
        if (nextTo == null) {
            newWeight = toWeight + stepLength;
        } else {
            newWeight = toWeight + (nextTo.getWeight() - toWeight) / 2;
        }

        if (Math.abs(newWeight - toWeight) <= 1) {
            throw new IllegalStateException(String.format(
                "down error, no enough weight :fromId:%d, toId:%d", fromId, toId));
        }
        from.setWeight(newWeight);
    }

    @SuppressWarnings("unchecked")
    public void reweight() {
        int batchSize = 100;
        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        Pageable pageable = new PageRequest(0, batchSize, sort);
        Page<M> page = findAll(pageable);
        do {
            //doReweight��ҪrequiresNew����
            ((BaseMovableService) AopContext.currentProxy()).doReweight(page);

            RepositoryHelper.clear();

            if (page.isLastPage()) {
                break;
            }

            pageable = new PageRequest((pageable.getPageNumber() + 1) * batchSize, batchSize, sort);

            page = findAll(pageable);

        } while (true);
    }

    public void doReweight(Page<M> page) {
        int totalElements = (int) page.getTotalElements();
        List<M> moves = page.getContent();

        int firstElement = page.getNumber() * page.getSize();

        for (int i = 0; i < moves.size(); i++) {
            M move = moves.get(i);
            move.setWeight((totalElements - firstElement - i) * stepLength);
            update(move);
        }

    }

    private Integer findNextWeight() {
        Searchable searchable = Searchable.newSearchable().setPage(0, 1)
            .addSort(Sort.Direction.DESC, "weight");
        Page<M> page = findAll(searchable);

        if (!page.hasContent()) {
            return stepLength;
        }

        return page.getContent().get(0).getWeight() + stepLength;
    }

    public M findPreByWeight(Integer weight) {
        Pageable pageable = new PageRequest(0, 1);
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_lt", weight);
        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        Page<M> page = findAll(Searchable.newSearchable(searchParams).addSort(sort)
            .setPage(pageable));

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    public M findNextByWeight(Integer weight) {
        Pageable pageable = new PageRequest(0, 1);

        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gt", weight);
        Sort sort = new Sort(Sort.Direction.ASC, "weight");
        Page<M> page = findAll(Searchable.newSearchable(searchParams).addSort(sort)
            .setPage(pageable));

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    private Long countByBetween(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);
        return count(Searchable.newSearchable(searchParams));
    }

    //@Query(value = "from Move m where m.weight>=?1 and m.weight <= ?2 order by m.weight asc")
    List<M> findByBetweenAndAsc(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);

        return findAllWithSort(Searchable.newSearchable(searchParams).addSort(Sort.Direction.ASC,
            "weight"));
    }

    List<M> findByBetweenAndDesc(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);

        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        return findAllWithSort(Searchable.newSearchable(searchParams).addSort(sort));
    }

}
