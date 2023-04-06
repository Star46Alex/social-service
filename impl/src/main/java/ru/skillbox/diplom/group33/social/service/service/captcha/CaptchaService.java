package ru.skillbox.diplom.group33.social.service.service.captcha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.group33.social.service.dto.auth.RegistrationDto;
import ru.skillbox.diplom.group33.social.service.dto.captcha.CaptchaDto;
import ru.skillbox.diplom.group33.social.service.dto.changeEmail.ChangeEmailDto;
import ru.skillbox.diplom.group33.social.service.model.captcha.Captcha;
import ru.skillbox.diplom.group33.social.service.repository.captcha.CaptchaRepository;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;



@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository captchaRepository;
    private final String IMAGE_FORMAT = "png";



    public CaptchaDto getCaptcha() {
        Captcha captcha = createCaptcha();
        captcha.setTime(ZonedDateTime.now());
        captchaRepository.save(captcha);

        CaptchaDto captchaDto = new CaptchaDto();
        captchaDto.setSecret(captcha.getSecret().toString());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(captcha.getImage(), IMAGE_FORMAT, bos);
            String prefix = "data:image/" + IMAGE_FORMAT + ";base64, ";
            captchaDto.setImage(prefix + Base64.getEncoder().encodeToString(bos.toByteArray()));
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return captchaDto;
    }

    private Captcha createCaptcha() {
        return new CaptchaBuilder()
                .createImage(121, 45)
                .setText(20, 30, 30, 4)
                .setLines(10)
                .build();
    }

    public boolean passCaptcha(RegistrationDto registrationDto) {
        Captcha captchaOrigin = captchaRepository.getById(UUID.fromString(registrationDto.getToken()));
        boolean passCaptcha = captchaOrigin.getCode().equals(registrationDto.getCode());
        log.info("In CaptchaService passCaptcha: {}", passCaptcha);
        return passCaptcha;
    }
    public boolean passCaptcha(ChangeEmailDto changeEmailDto){
        Captcha captchaOrigin = captchaRepository.getById(UUID.fromString(changeEmailDto.getTemp()));
        boolean passCaptcha = captchaOrigin.getCode().equals(changeEmailDto.getCode());
        log.info("In CaptchaService passCaptcha: {}", passCaptcha);
        return passCaptcha;

    }

}
