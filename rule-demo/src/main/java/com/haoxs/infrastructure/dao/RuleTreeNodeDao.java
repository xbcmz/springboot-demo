package com.haoxs.infrastructure.dao;

import com.haoxs.infrastructure.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 规则树节点DAO
 * @author: 小傅哥，微信：fustack
 * @date: 2021/9/22
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Mapper
public interface RuleTreeNodeDao {

    /**
     * 查询规则树节点
     *
     * @param treeId 规则树ID
     * @return 规则树节点集合
     */
    @Select("select id, tree_id, node_type, node_value, rule_key, rule_desc from rule_tree_node where tree_id = #{treeId}")
    List<RuleTreeNode> queryRuleTreeNodeList(Long treeId);

    /**
     * 查询规则树节点数量
     *
     * @param treeId 规则树ID
     * @return 节点数量
     */
    int queryTreeNodeCount(Long treeId);

    /**
     * 查询规则树节点
     *
     * @param treeId 规则树ID
     * @return 节点集合
     */
    List<RuleTreeNode> queryTreeRulePoint(Long treeId);

}
