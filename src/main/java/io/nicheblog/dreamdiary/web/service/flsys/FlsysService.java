package io.nicheblog.dreamdiary.web.service.flsys;

import io.nicheblog.dreamdiary.web.model.flsys.FlsysCmmDto;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysDirDto;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysFileDto;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysMetaDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * FlsysService
 * <pre>
 *  파일시스템 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("flsysService")
@Log4j2
public class FlsysService {

    @Resource(name = "flsysMetaService")
    public FlsysMetaService flsysMetaService;

    public FlsysCmmDto getFlsysByPath(final String filePath) throws Exception {
        Path path = Paths.get(filePath);
        File file = path.toFile();
        FlsysCmmDto dto = new FlsysCmmDto(file);

        // TODO: 메타정보 조회
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("upperFilePath", filePath);
        }};
        Page<FlsysMetaDto> metaPage = flsysMetaService.getListDto(searchParamMap, Pageable.unpaged());
        List<FlsysMetaDto> metaList = metaPage.getContent();

        File[] files = file.listFiles();
        if (files == null) return dto;
        Arrays.sort(files, Comparator.comparing(File::getName));
        List<FlsysDirDto> dirList = new ArrayList<>();
        for (File f : files) {
            if (!f.isDirectory()) continue;
            FlsysDirDto flsysDir = new FlsysDirDto(f);
            metaList.stream()
                    .filter(e -> e.getFilePath()
                                  .equals(f.getPath()
                                           .replace("\\", "/")))
                    .findFirst()
                    .ifPresent(flsysDir::setMeta);
            dirList.add(flsysDir);
        }
        dto.setDirList(dirList);

        List<FlsysFileDto> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) continue;
            FlsysFileDto flsysFile = new FlsysFileDto(f);

            // String contentType = new MimetypesFileTypeMap().getContentType(f);
            String fileExtn = f.getName()
                               .substring(f.getName()
                                           .lastIndexOf('.') + 1);
            List<String> vodExtnList = List.of(new String[]{"mp3", "mp4", "mov", "avi", "webm", "webp", "wmv", "flv"});
            boolean isVod = vodExtnList.contains(fileExtn);
            flsysFile.setIsVod(isVod);

            metaList.stream()
                    .filter(e -> e.getFilePath()
                                  .equals((f.getPath()
                                            .replace("\\", "/"))))
                    .findFirst()
                    .ifPresent(flsysFile::setMeta);
            fileList.add(flsysFile);
        }
        dto.setFileList(fileList);

        return dto;
    }

    // public void waiting() {
    //
    //     while(true) {
    //         String path = "["+file.getPath()+"] ";
    //         System.out.print(path);
    //         request = scan.nextLine();
    //
    //         if (request.indexOf(" ")!= -1){
    //             st = new StringTokenizer(request);
    //             int num = st.countTokens();
    //             one = st.nextToken();
    //             two = st.nextToken();
    //             if (st.hasMoreTokens())
    //                 three = st.nextToken();
    //
    //             if (num == 2) {
    //
    //                 if (one.equals("cd")) {
    //                     for (int i=0; i<list.length;i++) {
    //                         File f = list[i];
    //
    //                         if (two.equals(f.getName())) {
    //                             two = file.getPath()+"\\"+two;
    //                             file = new File(two);
    //                             view();
    //                         }
    //                         else
    //                             continue;
    //                     }
    //                 }
    //                 else if (one.equals("mkdir")) {
    //
    //                     two = file.getPath()+"\\"+two;
    //                     File newFile = new File(two);
    //                     if (newFile.exists())
    //                         System.out.println("존재하는 폴더입니다.");
    //                     else {
    //                         newFile.mkdir();
    //                         System.out.println(newFile.getName()+"폴더를 생성했습니다.");
    //                     }
    //                 }
    //                 else
    //                     System.out.println("잘못된 명령어입니다.");
    //             }
    //             else if (num == 3) {
    //
    //                 if (one.equals("rename")) {
    //
    //                     three = file.getPath()+"\\"+three;
    //                     File newFile = new File(three);
    //
    //                     if (newFile.exists())
    //                         System.out.println("이미 존재하는 폴더(파일)명입니다.");
    //                     else {
    //                         two = file.getPath()+"\\"+two;
    //                         File oldFile = new File(two);
    //                         oldFile.renameTo(newFile);
    //                         System.out.println(oldFile.getName()+"을 "+newFile.getName()+"으로 변경했습니다.");
    //                     }
    //
    //                 }
    //                 else
    //                     continue;
    //
    //             }
    //             else
    //                 System.out.println("잘못된 명령어입니다.");
    //         }
    //
    //         else {
    //             if (request.equals("exit")) {
    //                 System.out.println("종료합니다.");
    //                 scan.close();
    //                 break;
    //             }
    //             else if (request.equals("dir"))
    //                 view();
    //             else if (request.equals("..")) {
    //                 String nowPath = file.getPath();
    //
    //                 if (nowPath.equals("c:\\"))
    //                     System.out.println("최상위 폴더입니다.");
    //                 else {
    //                     nowPath = nowPath.substring(0, nowPath.lastIndexOf("\\"));
    //                     if (nowPath.equals("c:"))
    //                         nowPath = nowPath+"\\";
    //                     file = new File(nowPath);
    //                     view();
    //                 }
    //             }
    //             else
    //                 continue;
    //         }
    //     }
}
