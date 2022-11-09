package cs107;

import java.util.ArrayList;

import static cs107.QOISpecification.*;

/**
 * "Quite Ok Image" Encoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Second task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder() {
    }

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     *
     * @param image (Helper.Image) - Image to use
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *                        (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     */
    public static byte[] qoiHeader(Helper.Image image) {
        assert image.data() != null; //assert image is not null
        assert ((image.channels() == QOISpecification.RGB) || (image.channels() == QOISpecification.sRGB)); //assert that the number of channels encoding the image does not differ from the values of the constants QOISpecification.RGB and QOISpecification.RGBA
        assert (image.color_space() == QOISpecification.sRGB) || (image.color_space() == QOISpecification.ALL); //assert that the value encoding the color space does not differ from the values QOISpecification.sRGB and QOISpecification.ALL

        byte[] header = new byte[14]; //array that is going to contain the header
        byte[] magicNum = QOISpecification.QOI_MAGIC; //array containing the magic number
        for (int i = 0; i < 4; i++) {
            header[i] = magicNum[i]; //adding the magic number to the header array in the first 4 bytes
        }

        int height = (image.data()).length; //height of image
        int width = (image.data())[0].length; //width of image
        byte[] byteHeight = ArrayUtils.fromInt(height); //4 byte array containing height
        byte[] byteWidth = ArrayUtils.fromInt(width); //4 byte array containing width

        for (int i = 4; i < 8; i++) {
            header[i] = byteWidth[i - 4]; //adding the width of the image to the header array to the next 4 bytes
        }
        for (int i = 8; i < 12; i++) {
            header[i] = byteHeight[i - 8]; //adding the height of the image to the header array to the next 4 bytes
        }

        header[12] = image.channels(); //adding the number of channels that the image has to the next byte
        header[13] = image.color_space(); //adding the color-space of the channels that the image has to the next byte

        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     *
     * @param pixel (byte[]) - The Pixel to encode
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGB(byte[] pixel) {
        assert pixel.length == 4; //makes sure size of pixel is 4
        byte[] qoiOpRGB = new byte[4]; //array containing the qoiOpRGB
        qoiOpRGB[0] = QOI_OP_RGB_TAG; //first byte of array is used to store the tag
        for (int i = 0; i < 3; i++) {
            qoiOpRGB[i + 1] = pixel[i]; //other 3 bytes of array are used to store the RGB part of the pixel excluding Alpha
        }
        return qoiOpRGB;
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     *
     * @param pixel (byte[]) - The pixel to encode
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGBA(byte[] pixel) {
        assert pixel.length == 4; //makes sure size of pixel is 4
        byte[] qoiOpRGBA = new byte[5]; //array containing the qoiOpRGBA
        qoiOpRGBA[0] = QOI_OP_RGBA_TAG; //first byte of array is used to store the tag
        for (int i = 0; i < 4; i++) {
            qoiOpRGBA[i + 1] = pixel[i]; //other 4 bytes of array are used to store the pixel channels in RGBA
        }
        return qoiOpRGBA;
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     *
     * @param index (byte) - Index of the pixel
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     * @throws AssertionError if the index is outside the range of all possible indices
     */
    public static byte[] qoiOpIndex(byte index) {
        assert index >= 0; //assert index is positive
        assert index < 64; //assert index is smaller than 64

        byte[] qoiOpIndex = new byte[1]; //array that will contain the qoiOpIndex
        byte tag = QOI_OP_INDEX_TAG; //tag
        qoiOpIndex[0] = (byte) (tag | index); //bitwise or to join tag and index into one byte, then put this byte into qoiOpIndex array
        return qoiOpIndex;
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpDiff(byte[] diff){
        assert diff != null; //assert input in not null
        assert diff.length == 3; //assert the input is length 3
        for (int i = 0; i < 3; i++) {
            assert ((diff[i] > -3) && (diff[i] < 2)); //assert the diff is between -3 < x < 2
        }

        byte[] qoiOPDiff = new byte[1]; //array containing qoiOpDiff
        byte tag = QOI_OP_DIFF_TAG; //byte containing the tag
        int r = ((int) (diff[0])) + 2; //converts red diff into integer and adds two (as per instructions)
        int g = ((int) (diff[1])) + 2; //converts green diff into integer and adds two (as per instructions)
        int b = ((int) (diff[2])) + 2; //converts blue diff into integer and adds two (as per instructions)


        byte dr = (byte) ((byte) (r) << 4); //converts red diff into byte again and shifts 4 to the left
        byte dg = (byte) ((byte) (g) << 2); //converts green diff into byte again and shifts 2 to the left
        byte db = (byte) (b); //converts blue diff into byte again.

        qoiOPDiff[0] = (byte) (tag | dr | dg | db); //bitwise or to join the tag with all the colour diffs

        return qoiOPDiff;
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints
     *                        or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpLuma(byte[] diff) {
        assert diff != null; //assert input in not null
        assert diff.length == 3; //assert the input is length 3
        assert ((diff[1] > -33)&&(diff[1] < 32)); //assert dg is between -33 < x < 32
        assert ((diff[0]-diff[1]) > -9)&&((diff[0]-diff[1]) < 8); //assert dr' - dg' is between -9 < x < 8
        assert ((diff[2]-diff[1]) > -9)&&((diff[2]-diff[1]) < 8); //assert db' - dg' is between -9 < x < 8

        byte [] qoiOpLuma = new byte[2]; //array that will contain qoiOpLuma
        byte tag = QOI_OP_LUMA_TAG; //byte containing tag

        int g = diff[1] + 32; //int containing difference in green channel and offset by 32
        int rg = (diff[0]-diff[1]) + 8; //int containing subtraction of diff in red and diff in green and offset by 8
        int bg = (diff[2]-diff[1]) + 8; //int containing subtraction of diff in red and diff in green and offset by 8

        byte dg = (byte) g; //byte containing difference in green channel and offset by 32
        byte drdg = (byte) rg; //byte containing subtraction of diff in red and diff in green and offset by 8
        drdg = (byte) (drdg << 4); //push drdg to the left by 4
        byte dbdg = (byte) bg; //byte containing subtraction of diff in red and diff in green and offset by 8

        qoiOpLuma[0] = (byte) (tag | dg); //first byte is bitwise or of tag and diff green
        qoiOpLuma[1] = (byte) (drdg | dbdg); //second byte is bitwise or of shifted drdg and dbdg

        return qoiOpLuma;
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     *
     * @param count (byte) - Number of similar pixels
     * @return (byte[]) - Encoding of count
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     */
    public static byte[] qoiOpRun(byte count) {
        assert ((count < 63)&&(count > 0)); //assert count is between 0 < x < 63

        byte tag = QOI_OP_RUN_TAG; //byte containing tag
        byte [] qoiOpRun = new byte[1]; //byte array containing qoiOpRun
        count = (byte)(((int)count)-1); // offset count by -1
        qoiOpRun[0] = (byte) (tag | count); //make qoiOpRun be equal to the bitwise or of tag and count
        return qoiOpRun;
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     *
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image) {

        assert image != null;
        //assert picture != null DON'T KNOW WHAT PICTURE IS
        for (int i = 0; i < image.length; i++) {
            assert image[i].length == 4; //assert all pixels are size 4
        }


        byte[] prevPixel = QOISpecification.START_PIXEL; //first pixel is always constant (as per instructions)
        byte[][] hash = new byte[64][4]; //initialization of hash table
        int count; //used for qoiOpRun
        ArrayList<Byte> encodedata = new ArrayList<>();//arrayList where the encoded data is going to be stored




        for (int i = 0; i < image.length; i++) { //for every pixel


            /*===============================
            ======== previous pixel =========
            =================================*/

            if (i != 0){
                prevPixel = image[i-1];
            }

            /*===============================
            ======== hash index =========
            =================================*/

            byte pixHash = QOISpecification.hash(image[i]);

            /*================================
            ==difference between two pixels ==
            =================================*/

            byte [] diff = new byte[4];
            for (int j = 0; j < 3; j++){
                diff[j] = (byte) (image[i][j] - prevPixel[j]);
            }

          /*===========================
            ======== qoiOpRun =========
            ===========================*/

            if (image[i] == prevPixel){
                count = 0; //restart count
                while ((image[i] == prevPixel) && (count < 62) && (count >= 0) && ((i == image.length-1))) {
                    //while the pixel equals the previous pixel and count is less than 62 and the pixel that is being compared is not the last pixel.
                    i++; //next column
                    count++; //add one to the qoiOpRun count
                }
                if (i == image.length - 1) { //if the compared pixel was the last pixel, the while loop breaks and the if activates to add the last pixel into the qoiOpRun
                    count++;
                }
                encodedata.add((qoiOpRun((byte)count))[0]); //add qoiOpRun byte to the list
                continue;
            }

            /*===========================
            ======== qoiOpIndex =========
            ===========================*/

            else if (hash[pixHash] == image[i]){
                encodedata.add((qoiOpIndex(pixHash))[0]);
            }
            else if (hash[pixHash] != image[i]){
                hash[pixHash] = image[i];
            }


        /*===========================
         ======== qoiOpDiff =========
        ===========================*/

            else if ((diff[3] == 0)&&(((diff[0]) > -3)&&((diff[0]) < 2))&&(((diff[1]) > -3)&&((diff[1]) < 2))&&(((diff[2]) > -3)&&((diff[2]) < 2))){
                encodedata.add(qoiOpDiff(diff)[0]);
            }


        }

        byte [] encodeData = new byte[encodedata.size()];
        for (int z = 0; z < encodedata.size(); z++){
            encodeData[z] = encodedata.get(z);
        }
        return encodeData;
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     *
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     */
    public static byte[] qoiFile(Helper.Image image) {
        return Helper.fail("Not Implemented");
    }

}