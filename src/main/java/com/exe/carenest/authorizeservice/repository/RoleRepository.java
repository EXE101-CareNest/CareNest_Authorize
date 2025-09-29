package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole_Name(String roleName);

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.module")
    List<RolePermission> findAllWithModule();


    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM RolePermission rp 
        WHERE rp.role.id = :roleId 
        AND rp.module.urlPattern = :modulePattern 
        AND rp.httpPermission = :permission
    )
    """)
    boolean hasPermission(@Param("roleId") Integer roleId,
                          @Param("modulePattern") String modulePattern,
                          @Param("permission") HttpPermission permission);

//    @Query("""
//    SELECT new com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplay(
//        m.urlPattern,\s
//        m.name,
//        STRING_AGG(CAST(rp.httpPermission AS string), ',')
//    )
//    FROM RolePermission rp
//    JOIN rp.module m
//    WHERE rp.role.id = :roleId
//    GROUP BY m.urlPattern, m.name
//    ORDER BY m.name
//   \s""")
//    List<RolePermissionDisplay> getRolePermissions(@Param("roleId") Integer roleId);


//    @Query(value = """
//    SELECT
//        m.url_pattern as moduleUrl,
//        m.name as moduleName,
//        GROUP_CONCAT(rp.http_permission ORDER BY rp.http_permission) as permissions
//    FROM role_permission rp
//    JOIN module m ON m.url_pattern = rp.module_url_pattern
//    WHERE rp.role_id = ?1
//    GROUP BY m.url_pattern, m.name
//    ORDER BY m.name
//    """, nativeQuery = true)
//    List<Object[]> getRolePermissionsNative(Integer roleId);


    //Fast check
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.name = :roleName AND rp.module.urlPattern = :actualUrl AND rp.httpPermission = :permission")
    RolePermission getRolePermissionByRoleAndModuleAndPermission(@Param("roleName") String roleName,
                                               @Param("actualUrl") String actualUrl,
                                               @Param("permission") HttpPermission permission);



    void deleteByRole_NameAndModule_UrlPattern(String roleName, String moduleUrlPattern);
}
