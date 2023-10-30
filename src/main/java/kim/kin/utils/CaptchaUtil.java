package kim.kin.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

/**
 * @author kin.kim
 * @since 2023-10-30
 **/
@Component
public class CaptchaUtil {
    @Value("encrypt-salt")
    private String encryptSalt;
    private final Random rand = new Random();

    public String encryptText(String captchaText, Integer timeout) {
        timeout = Optional.ofNullable(timeout).orElse(60);
        String yyyyMMddHHmmss = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return getEncrypt(yyyyMMddHHmmss, captchaText, timeout);
    }

    public void verifyEncryptText(String captchaText, String captchaEncrypt) throws Exception {
        String yyyyMMddHHmmss = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int length = yyyyMMddHHmmss.length();
        if (captchaEncrypt.length() > length) {
            int timeoutIndex = captchaEncrypt.indexOf("-");
            String sourceTime = captchaEncrypt.substring(0, length);
            String timeout = captchaEncrypt.substring(length, timeoutIndex);
            LocalDateTime sourceLocalDatime = LocalDateTime.parse(sourceTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            if (!sourceLocalDatime.plusSeconds(Long.parseLong(timeout)).isAfter(LocalDateTime.now())) {
                throw new Exception("验证超时,请刷新!");
            }
            if (!getEncrypt(sourceTime, captchaText, Integer.valueOf(timeout)).equals(captchaEncrypt)) {
                throw new Exception("验证码不正确");
            }
        } else {
            throw new Exception("captchaEncrypt 不可用");
        }
    }

    private String getEncrypt(String yyyyMMddHHmmss, String captchaText, Integer timeout) {
        return yyyyMMddHHmmss + timeout + "-" + DigestUtils.md5DigestAsHex((yyyyMMddHHmmss + timeout + captchaText + encryptSalt).getBytes());
    }

    /**
     * 生成随机颜色
     *
     * @param start [int]
     * @param end   [int]
     * @return Color  [object]
     */
    private Color getRandColor(int start, int end) {
        int randNum;
        if (start > 255) start = 255;
        if (end > 255) end = 255;
        if (start > end) randNum = start - end;
        else randNum = end - start;
        int r = start + rand.nextInt(randNum);
        int g = start + rand.nextInt(randNum);
        int b = start + rand.nextInt(randNum);
        return new Color(r, g, b);
    }


    /**
     * 着色\旋转\缩放
     *
     * @param word     文字
     * @param graphics 图片对象
     */
    private void coloredAndRotation(String word, int i, Graphics graphics) {
        graphics.setColor(new Color(20 + rand.nextInt(110), 20 + rand.nextInt(110), 20 + rand.nextInt(110)));
        Graphics2D g2d = (Graphics2D) graphics;
        AffineTransform trans = new AffineTransform();
        trans.rotate(rand.nextInt(45) * 3.14 / 180, 15 * i + 8, 7);
        float scaleSize = rand.nextFloat() + 0.8f;
        if (scaleSize > 1f) scaleSize = 1f;
        trans.scale(scaleSize, scaleSize);
        g2d.setTransform(trans);
        graphics.drawString(word, 15 * i + 20, 20);
    }

    /**
     * 生成100条干扰线
     *
     * @param graphics2D graphics2D
     * @param width      width
     * @param height     width
     */
    private void getRandLine(Graphics2D graphics2D, int width, int height) {
        for (int i = 0; i < 100; i++) {
            int x = rand.nextInt(width - 1);
            int y = rand.nextInt(height - 1);
            int z = rand.nextInt(6) + 1;
            int w = rand.nextInt(12) + 1;

            BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
            Line2D line = new Line2D.Double(x, y, x + z, y + w);
            graphics2D.setStroke(bs);
            graphics2D.draw(line);
        }
    }

    /**
     * 获取随机文字
     *
     * @param length   [int]        验证码长度
     * @param graphics [Graphics]   图片对象
     * @return String
     * @ 1:A-Z
     * @ other:0-9
     */
    private String getRandWord(int length, Graphics graphics) {
        StringBuilder finalWord = new StringBuilder();
        String firstWord;
        int tempInt;
        for (int i = 0; i < length; i++) {
            if (rand.nextInt(2) == 1) {
                tempInt = rand.nextInt(26) + 65;
            } else {
                tempInt = rand.nextInt(10) + 48;
            }
            firstWord = String.valueOf((char) tempInt);
            finalWord.append(firstWord);
            this.coloredAndRotation(firstWord, i, graphics);
        }

        return finalWord.toString();
    }

    public record CaptchaRecord(String randCode, String base64Img) {
    }

    public CaptchaRecord genCaptcha(Integer width, Integer height) throws IOException {
        width = Optional.ofNullable(width).orElse(100);
        height = Optional.ofNullable(height).orElse(40);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        Font mFont = new Font("宋体", Font.BOLD, 22);
        graphics.setColor(this.getRandColor(200, 250));
        graphics.fillRect(0, 0, width, height);
        graphics.setFont(mFont);
        graphics.setColor(this.getRandColor(180, 200));
        this.getRandLine(graphics2D, width, height);
        String randCode = this.getRandWord(4, graphics);
        graphics.dispose();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", stream);
//        ImageIO.write(bufferedImage, "png", Files.newOutputStream(Paths.get("d:\\code\\" + randCode + ".png")));
        String base64imgAppend = "data:image/png;base64,";
        String base64Str = base64imgAppend + Base64.getEncoder().encodeToString(stream.toByteArray());
        return new CaptchaRecord(randCode, base64Str);
    }


    public static void main(String[] args) throws IOException {
        CaptchaUtil captchaUtil = new CaptchaUtil();
        for (int i = 0; i < 10; i++) {
            CaptchaRecord captchaRecord = captchaUtil.genCaptcha(101, 40);
            System.out.println(captchaRecord);
        }

    }
}