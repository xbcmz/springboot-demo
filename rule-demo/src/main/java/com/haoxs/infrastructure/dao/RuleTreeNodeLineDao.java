package com.haoxs.infrastructure.dao;

import com.haoxs.infrastructure.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 规则树节点连线DAO
 * @author: 小傅哥，微信：fustack
 * @date: 2021/9/22
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Mapper
public interface RuleTreeNodeLineDao {

    /**
     * 查询规则树节点连线集合
     *
     * @param req 入参
     * @return 规则树节点连线集合
     */
    @Select("SELECT id, tree_id, node_id_from, node_id_to, rule_limit_type, rule_limit_value FROM rule_tree_node_line where tree_id = #{treeId} and node_id_from = #{nodeIdFrom}")
    List<RuleTreeNodeLine> queryRuleTreeNodeLineList(RuleTreeNodeLine req);

    /**
     * 查询规则树连线数量
     *
     * @param treeId 规则树ID
     * @return 规则树连线数量
     */
    int queryTreeNodeLineCount(Long treeId);

}
