package io.metersphere.sql.service.common;

import org.springframework.web.multipart.MultipartFile;

public interface JarPackagesManageService {

    String uploadFile(MultipartFile file) throws Exception ;

    byte[] getFile(String fileName) throws Exception ;

}
