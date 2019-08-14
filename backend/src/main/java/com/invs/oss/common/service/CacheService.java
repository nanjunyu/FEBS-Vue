package com.invs.oss.common.service;

import com.invs.oss.common.exception.RedisConnectException;
import com.invs.oss.system.domain.Menu;
import com.invs.oss.system.domain.Role;
import com.invs.oss.system.domain.User;
import com.invs.oss.system.domain.UserConfig;

import java.util.List;
import java.util.Map;

public interface CacheService {

    /**
     * 测试 Redis是否连接成功
     */
    void testConnect() throws Exception;

    /**
     * 从缓存中获取用户
     *
     * @param username 用户名
     * @return User
     */
    User getUser(String username) throws Exception;

    /**
     * 从缓存中获取用户角色
     *
     * @param username 用户名
     * @return 角色集
     */
    List<Role> getRoles(String username) throws Exception;

    /**
     * 从缓存中获取用户权限
     *
     * @param username 用户名
     * @return 权限集
     */
    List<Menu> getPermissions(String username) throws Exception;

    /**
     * 从缓存中获取用户个性化配置
     *
     * @param userId 用户 ID
     * @return 个性化配置
     */
    UserConfig getUserConfig(String userId) throws Exception;

    /**
     * 缓存用户信息，只有当用户信息是查询出来的，完整的，才应该调用这个方法
     * 否则需要调用下面这个重载方法
     *
     * @param user 用户信息
     */
    void saveUser(User user) throws Exception;

    /**
     * 缓存用户信息
     *
     * @param username 用户名
     */
    void saveUser(String username) throws Exception;

    /**
     * 缓存用户角色信息
     *
     * @param username 用户名
     */
    void saveRoles(String username) throws Exception;

    /**
     * 缓存用户权限信息
     *
     * @param username 用户名
     */
    void savePermissions(String username) throws Exception;

    /**
     * 缓存用户个性化配置
     *
     * @param userId 用户 ID
     */
    void saveUserConfigs(String userId) throws Exception;

    /**
     * 删除用户信息
     *
     * @param username 用户名
     */
    void deleteUser(String username) throws Exception;

    /**
     * 删除用户角色信息
     *
     * @param username 用户名
     */
    void deleteRoles(String username) throws Exception;

    /**
     * 删除用户权限信息
     *
     * @param username 用户名
     */
    void deletePermissions(String username) throws Exception;

    /**
     * 删除用户个性化配置
     *
     * @param userId 用户 ID
     */
    void deleteUserConfigs(String userId) throws Exception;


    /**
    *
    *  缓存上传到fastDfs的附件信息
    * @Author Frank
    * @Date Create in  2019/5/27 10:27
    * @param fileId 附件id
    * @param fastPath fastDfs保存的地址
    * @return
    */
    void saveFastDfsUrl(String fileId,String fastPath) throws Exception;

    void deleteFastDfsUrl(String fileId) throws Exception;

    Long setFastDfsPv(String fileId,String value) throws RedisConnectException;

    String  getFastDfsUrlByFileId(String fileId) throws Exception;

    Long increaseFilePv(String fileId) throws RedisConnectException;

    Map<String, String> getAllFastPv() throws RedisConnectException;

    String getFastDfsPvByFileId(String fileId) throws RedisConnectException;


    void deleteFastDfsPv(String fields) throws RedisConnectException;
}
