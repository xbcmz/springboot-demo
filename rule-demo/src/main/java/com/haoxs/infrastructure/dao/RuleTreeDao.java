package com.haoxs.infrastructure.dao;

import com.haoxs.infrastructure.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 规则树配置DAO
 * @author: 小傅哥，微信：fustack
 * @date: 2021/9/22
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Mapper
public interface RuleTreeDao {

    /**
     * 规则树查询
     *
     * @param id ID
     * @return 规则树
     */

    @Select("select id, tree_name, tree_desc, tree_root_node_id, create_time, update_time from rule_tree where id = #{treeId}")
    RuleTree queryRuleTreeByTreeId(Long id);

    /**
     * 规则树简要信息查询
     *
     * @param treeId 规则树ID
     * @return 规则树
     */
    RuleTree queryTreeSummaryInfo(Long treeId);

}