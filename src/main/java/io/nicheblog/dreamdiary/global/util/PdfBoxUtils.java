package io.nicheblog.dreamdiary.global.util;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.utils.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * PdfBoxUtils
 * <pre>
 *  Image -> PDF 관련 처리 유틸리티 모듈
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class PdfBoxUtils {

    /**
     * 이미지 파일 묶음 PDF 다운로드
     */
    public static void imgCmbnPdfDonwload(
            final String fileNm,
            final List<AtchFileDtlDto> fileList
    ) throws Exception {
        PDDocument doc = new PDDocument();

        try {
            // 폴더 생성
            String pdfPath = "local/pdf_temp/";
            File file = new File(pdfPath);
            if (!file.exists()) file.mkdirs();

            for (AtchFileDtlDto atchFile : fileList) {
                String imagePath = atchFile.getFileStrePath() + "/" + atchFile.getStreFileNm();
                File imageFile = new File(atchFile.getFileStrePath(), atchFile.getStreFileNm());
                if (!imageFile.exists()) throw new FileNotFoundException("파일이 존재하지 않습니다.");
                if (!imageFile.canRead()) throw new SecurityException("파일을 읽을 권한이 없습니다.");
                BufferedImage awtImage = ImageIO.read(imageFile);

                PDPage pdPage = new PDPage();
                doc.addPage(pdPage);

                int pageWidth = Math.round(pdPage.getCropBox()
                                                 .getWidth());
                int pageHeight = Math.round(pdPage.getCropBox()
                                                  .getHeight());
                float imgRatio = 1;
                int orgnlImgWidth = awtImage.getWidth(null);
                int orgnlImgHeight = awtImage.getHeight(null);
                if (pageWidth < orgnlImgWidth) imgRatio = (float) orgnlImgWidth / (float) pageWidth;
                //설정된 비율로 이미지 리사이징
                int imgWidth = Math.round(orgnlImgWidth / imgRatio);
                int imgHeight = Math.round(orgnlImgHeight / imgRatio);
                //이미지를 가운데 정렬하기 위해 좌표 설정
                int pageWidthPosition = (pageWidth - imgWidth) / 2;
                int pageHeightPosition = (pageHeight - imgHeight) / 2;

                PDRectangle pageSize = pdPage.getMediaBox();
                if ((pageSize.getWidth() <= pageSize.getHeight()) && (pdPage.getRotation() == 90 || pdPage.getRotation() == 270)) {
                    pdPage.setRotation(0);      //Rotate Landscape
                } else {
                    pdPage.setRotation(90);     //Rotate Portrait
                }
                pdPage.setRotation(0);

                PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
                PDPageContentStream contents = new PDPageContentStream(doc, pdPage);

                contents.drawImage(pdImage, pageWidthPosition, pageHeightPosition, imgWidth, imgHeight);
                contents.close();
                doc.save(pdfPath + fileNm);
            }

            // 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
            FileUtils.setRespnsHeader(fileNm);
            CookieUtils.setFileDownloadSuccessCookie();

            File pdfFile = new File(pdfPath, fileNm);
            FileUtils.downloadFile(pdfFile, fileNm);
        } catch (Exception e) {
            String resultMsg = MessageUtils.getExceptionMsg(e);
            MessageUtils.alertMessage(resultMsg);
        }
    }
}
