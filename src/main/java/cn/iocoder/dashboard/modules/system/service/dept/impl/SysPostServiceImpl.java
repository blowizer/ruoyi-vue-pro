package cn.iocoder.dashboard.modules.system.service.dept.impl;

import cn.iocoder.dashboard.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.dashboard.common.pojo.PageResult;
import cn.iocoder.dashboard.modules.system.controller.dept.vo.post.SysPostCreateReqVO;
import cn.iocoder.dashboard.modules.system.controller.dept.vo.post.SysPostExportReqVO;
import cn.iocoder.dashboard.modules.system.controller.dept.vo.post.SysPostPageReqVO;
import cn.iocoder.dashboard.modules.system.controller.dept.vo.post.SysPostUpdateReqVO;
import cn.iocoder.dashboard.modules.system.convert.dept.SysPostConvert;
import cn.iocoder.dashboard.modules.system.dal.mysql.dept.SysPostMapper;
import cn.iocoder.dashboard.modules.system.dal.dataobject.dept.SysPostDO;
import cn.iocoder.dashboard.modules.system.service.dept.SysPostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.dashboard.modules.system.enums.SysErrorCodeConstants.*;

/**
 * 岗位 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class SysPostServiceImpl implements SysPostService {

    @Resource
    private SysPostMapper postMapper;

    @Override
    public List<SysPostDO> listPosts(Collection<Long> ids, Collection<Integer> statuses) {
        return postMapper.selectList(ids, statuses);
    }

    @Override
    public PageResult<SysPostDO> pagePosts(SysPostPageReqVO reqVO) {
        return postMapper.selectPage(reqVO);
    }

    @Override
    public List<SysPostDO> listPosts(SysPostExportReqVO reqVO) {
        return postMapper.selectList(reqVO);
    }

    @Override
    public SysPostDO getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public Long createPost(SysPostCreateReqVO reqVO) {
        // 校验正确性
        this.checkCreateOrUpdate(null, reqVO.getName(), reqVO.getCode());
        // 插入岗位
        SysPostDO post = SysPostConvert.INSTANCE.convert(reqVO);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(SysPostUpdateReqVO reqVO) {
        // 校验正确性
        this.checkCreateOrUpdate(reqVO.getId(), reqVO.getName(), reqVO.getCode());
        // 更新岗位
        SysPostDO updateObj = SysPostConvert.INSTANCE.convert(reqVO);
        postMapper.updateById(updateObj);
    }

    private void checkCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        checkPostExists(id);
        // 校验岗位名的唯一性
        checkPostNameUnique(id, name);
        // 校验岗位编码的唯一性
        checkPostCodeUnique(id, code);
    }

    private void checkPostNameUnique(Long id, String name) {
        SysPostDO post = postMapper.selectByName(name);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw ServiceExceptionUtil.exception(POST_NAME_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw ServiceExceptionUtil.exception(POST_NAME_DUPLICATE);
        }
    }

    private void checkPostCodeUnique(Long id, String code) {
        SysPostDO post = postMapper.selectByCode(code);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw ServiceExceptionUtil.exception(POST_CODE_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw ServiceExceptionUtil.exception(POST_CODE_DUPLICATE);
        }
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        this.checkPostExists(id);
        // 删除部门
        postMapper.deleteById(id);
    }

    private void checkPostExists(Long id) {
        if (id == null) {
            return;
        }
        SysPostDO post = postMapper.selectById(id);
        if (post == null) {
            throw ServiceExceptionUtil.exception(POST_NOT_FOUND);
        }
    }

}
