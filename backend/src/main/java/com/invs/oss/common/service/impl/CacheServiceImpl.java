package com.invs.oss.common.service.impl;

import com.invs.oss.common.exception.RedisConnectException;
import com.invs.oss.common.service.CacheService;
import com.invs.oss.system.dao.UserMapper;
import com.invs.oss.system.domain.Menu;
import com.invs.oss.system.domain.Role;
import com.invs.oss.system.domain.User;
import com.invs.oss.system.domain.UserConfig;
import com.invs.oss.system.service.MenuService;
import com.invs.oss.system.service.RoleService;
import com.invs.oss.system.service.UserConfigService;
import com.invs.oss.system.service.UserService;
import com.invs.oss.common.domain.FebsConstant;
import com.invs.oss.common.service.RedisService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserConfigService userConfigService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void testConnect() throws Exception {
        this.redisService.exists("test");
    }

    @Override
    public User getUser(String username) throws Exception {
        String userString = this.redisService.get(FebsConstant.USER_CACHE_PREFIX + username);
        if (StringUtils.isBlank(userString))
            throw new Exception();
        else
            return this.mapper.readValue(userString, User.class);
    }

    @Override
    public List<Role> getRoles(String username) throws Exception {
        String roleListString = this.redisService.get(FebsConstant.USER_ROLE_CACHE_PREFIX + username);
        if (StringUtils.isBlank(roleListString)) {
            throw new Exception();
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Role.class);
            return this.mapper.readValue(roleListString, type);
        }
    }

    @Override
    public List<Menu> getPermissions(String username) throws Exception {
        String permissionListString = this.redisService.get(FebsConstant.USER_PERMISSION_CACHE_PREFIX + username);
        if (StringUtils.isBlank(permissionListString)) {
            throw new Exception();
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Menu.class);
            return this.mapper.readValue(permissionListString, type);
        }
    }

    @Override
    public UserConfig getUserConfig(String userId) throws Exception {
        String userConfigString = this.redisService.get(FebsConstant.USER_CONFIG_CACHE_PREFIX + userId);
        if (StringUtils.isBlank(userConfigString))
            throw new Exception();
        else
            return this.mapper.readValue(userConfigString, UserConfig.class);
    }

    @Override
    public void saveUser(User user) throws Exception {
        String username = user.getUsername();
        this.deleteUser(username);
        redisService.set(FebsConstant.USER_CACHE_PREFIX + username, mapper.writeValueAsString(user));
    }

    @Override
    public void saveUser(String username) throws Exception {
        User user = userMapper.findDetail(username);
        this.deleteUser(username);
        redisService.set(FebsConstant.USER_CACHE_PREFIX + username, mapper.writeValueAsString(user));
    }

    @Override
    public void saveRoles(String username) throws Exception {
        List<Role> roleList = this.roleService.findUserRole(username);
        if (!roleList.isEmpty()) {
            this.deleteRoles(username);
            redisService.set(FebsConstant.USER_ROLE_CACHE_PREFIX + username, mapper.writeValueAsString(roleList));
        }

    }

    @Override
    public void savePermissions(String username) throws Exception {
        List<Menu> permissionList = this.menuService.findUserPermissions(username);
        if (!permissionList.isEmpty()) {
            this.deletePermissions(username);
            redisService.set(FebsConstant.USER_PERMISSION_CACHE_PREFIX + username, mapper.writeValueAsString(permissionList));
        }
    }

    @Override
    public void saveUserConfigs(String userId) throws Exception {
        UserConfig userConfig = this.userConfigService.findByUserId(userId);
        if (userConfig != null) {
            this.deleteUserConfigs(userId);
            redisService.set(FebsConstant.USER_CONFIG_CACHE_PREFIX + userId, mapper.writeValueAsString(userConfig));
        }
    }

    @Override
    public void deleteUser(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(FebsConstant.USER_CACHE_PREFIX + username);
    }

    @Override
    public void deleteRoles(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(FebsConstant.USER_ROLE_CACHE_PREFIX + username);
    }

    @Override
    public void deletePermissions(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(FebsConstant.USER_PERMISSION_CACHE_PREFIX + username);
    }

    @Override
    public void deleteUserConfigs(String userId) throws Exception {
        redisService.del(FebsConstant.USER_CONFIG_CACHE_PREFIX + userId);
    }

    @Override
    public void saveFastDfsUrl(String fileId, String fastPath) throws Exception {
        this.deleteFastDfsUrl(fileId);
        redisService.set(FebsConstant.OSS_FILE_URL_CACHE_PREFIX + fileId, fastPath);
    }

    @Override
    public void deleteFastDfsUrl(String fileId) throws Exception {
        redisService.del(FebsConstant.OSS_FILE_URL_CACHE_PREFIX + fileId);
    }

    @Override
    public Long setFastDfsPv(String fileId,String value) throws RedisConnectException {
        return redisService.hset(FebsConstant.OSS_FILE_PV_CACHE_PREFIX,fileId,value);
    }

    @Override
    public String getFastDfsUrlByFileId(String fileId) throws Exception {
        return redisService.get(FebsConstant.OSS_FILE_URL_CACHE_PREFIX + fileId);
    }

    @Override
    public Long increaseFilePv(String fileId) throws RedisConnectException {
        return redisService.incr(FebsConstant.OSS_FILE_PV_CACHE_PREFIX + fileId);
    }

    @Override
    public Map<String, String> getAllFastPv() throws RedisConnectException {
        Map<String, String> array = redisService.hgetAll(FebsConstant.OSS_FILE_PV_CACHE_PREFIX);
       return array;

    }

    @Override
    public String getFastDfsPvByFileId(String fileId) throws RedisConnectException {
        return redisService.hget(FebsConstant.OSS_FILE_PV_CACHE_PREFIX,fileId);
    }

    @Override
    public void deleteFastDfsPv(String fields) throws RedisConnectException {
        redisService.hdel(FebsConstant.OSS_FILE_PV_CACHE_PREFIX,fields);
    }

}
