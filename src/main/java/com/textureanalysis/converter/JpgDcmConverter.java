package com.textureanalysis.converter;


import org.dcm4che2.data.*;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.UIDUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 * Created by Monika on 2016-03-31.
 */
public class JpgDcmConverter {

    //private static Logger logger = LoggerFactory.getLogger(JpgDcmConverter.class);

    public void convert(File jpgFile, File dcmFile) {
        try {
            BufferedImage jpegImage = ImageIO.read(jpgFile);
            int colorComponents = jpegImage.getColorModel().getNumColorComponents();
            int bitsPerPixel = jpegImage.getColorModel().getPixelSize();
            int bitsAllocated = (bitsPerPixel / colorComponents);
            int samplesPerPixel = colorComponents;
            FileOutputStream fos = new FileOutputStream(dcmFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DicomOutputStream dos = new DicomOutputStream(bos);
            DicomObject dicom = createBasicDicomObject(jpegImage, samplesPerPixel, bitsAllocated);
            dos.writeDicomFile(dicom);
            dos.writeHeader(Tag.PixelData, VR.OB, -1);
            dos.writeHeader(Tag.Item, null, 0);
            int jpgLen = (int) jpgFile.length();
            dos.writeHeader(Tag.Item, null, (jpgLen+1)&~1);
            FileInputStream fis = new FileInputStream(jpgFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            byte[] buffer = new byte[65536];
            int b;
            while ((b = dis.read(buffer)) > 0) {
                dos.write(buffer, 0, b);
            }
            if ((jpgLen&1) != 0)
                dos.write(0);
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
            dos.close();
        } catch (Exception e) {
          //  logger.error(e.getMessage());
        }

    }

    public DicomObject createBasicDicomObject(BufferedImage bufferedImage, int samplesPerPixel, int bitsAllocated){
        DicomObject dicom = new BasicDicomObject();
        dicom.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");
        dicom.putString(Tag.PhotometricInterpretation, VR.CS, samplesPerPixel == 3 ? "YBR_FULL_422" : "MONOCHROME2");
        dicom.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
        dicom.putInt(Tag.Rows, VR.US, bufferedImage.getHeight());
        dicom.putInt(Tag.Columns, VR.US, bufferedImage.getWidth());
        dicom.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
        dicom.putInt(Tag.BitsStored, VR.US, bitsAllocated);
        dicom.putInt(Tag.HighBit, VR.US, bitsAllocated-1);
        dicom.putInt(Tag.PixelRepresentation, VR.US, 0);
        dicom.putDate(Tag.InstanceCreationDate, VR.DA, new Date());
        dicom.putDate(Tag.InstanceCreationTime, VR.TM, new Date());
        dicom.putString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
        dicom.putString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
        dicom.putString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
        dicom.initFileMetaInformation(UID.JPEGBaseline1);
        return dicom;
    }


}
