package fr.mcc.transverse.captcha.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;


@Controller
public class CaptchaController {
	private static final String IMAGE_JPEG_CONTENT_TYPE = "image/jpeg";

	private static final String AUDIO_X_WAV_CONTENT_TYPE = "audio/x-wav";

	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(CaptchaController.class);

	/**
	 * captcha service for image captcha
	 */
	private GenericManageableCaptchaService imageCaptchaService;
	/**
	 * captcha service for sound captcha
	 */
	private GenericManageableCaptchaService soundCaptchaService;

	/** image type jpg */
	private static final String IMAGE_TYPE_JPG = "jpg";

	/**
	 * Controller method for validating captcha
	 * @param challengeId challengeID (the captcha's ID)
	 * @param userResponse the user text response
	 * @param challengeType the challenge type (image or sound)
	 * @return "ok" if captcha is validated, "ko" otherwise
	 */
	@RequestMapping(value = "/validateCaptcha", method = RequestMethod.GET)
	public String validateCaptcha(@RequestParam String challengeId, @RequestParam String userResponse, @RequestParam String challengeType) {
		GenericManageableCaptchaService captacheService = null;
		if ("image".equalsIgnoreCase(challengeType)) {
			captacheService = imageCaptchaService;
		}
		captacheService = soundCaptchaService;
		if (validateChallenge(captacheService, challengeId, userResponse)) {
			return "ok";
		}
		return "ko";
	}

	/**
	 * Controller method for generating captcha image
	 * @param httpServletResponse the http serveur response to write image (autowired by spring mvc)
	 * @param challengeId challengeID (the captcha's ID)
	 */
	@RequestMapping(value = "/captcha/{challengeId}.jpg", method = RequestMethod.GET)
	public void generateImageCaptcha(HttpServletResponse httpServletResponse, @PathVariable String challengeId) {
		LOGGER.info("generate captcha image, challengeID : " + challengeId);
		writeHttpServletResponse(httpServletResponse, IMAGE_JPEG_CONTENT_TYPE, generateImageCaptchaAsBytes(challengeId));
	}
	
	/**
	 * Controller method for generating captcha sound
	 * @param httpServletResponse the http serveur response to write image (autowired by spring mvc)
	 * @param challengeId challengeID (the captcha's ID)
	 */
	@RequestMapping(value = "/captcha/{challengeId}.wav", method = RequestMethod.GET)
	public void generateSoundCaptcha(HttpServletResponse httpServletResponse, @PathVariable String challengeId) {
		LOGGER.info("generate captcha sound, challengeID : " + challengeId);
		writeHttpServletResponse(httpServletResponse, AUDIO_X_WAV_CONTENT_TYPE, generateSoundCaptchaAsBytes(challengeId));

	}
	/**
	 * Private method for validate a challenge
	 * @param captacheService the captcha service (image or sound)
	 * @param challengeId the captcha id
	 * @param userResponse the user response
	 * @return true if ok false otherwise. If the challengeId is unknown, then a warn log is print and the method return false
	 */
	private boolean validateChallenge(GenericManageableCaptchaService captacheService, String challengeId, String userResponse) {
		try {
			if (captacheService.validateResponseForID(challengeId, userResponse.toLowerCase())) {
				LOGGER.info("challenge " + challengeId + " OK");
				return true;
			}
			LOGGER.info("challenge " + challengeId + " KO");
			return false;
		} catch (CaptchaServiceException cse) {
			LOGGER.warn("error while validating captcha" + cse.getMessage());
		}
		return false;
	}

	/**
	 * Generate Image captcha
	 * 
	 * @param challengeId the challengeId, captcha's ID
	 * @return a byte array representing the captcha image
	 * @throws RuntimeException if error occurs
	 */
	private byte[] generateImageCaptchaAsBytes(String challengeId) throws RuntimeException {
		byte[] captchaByte = null;
		// the output stream to render the captcha image as jpeg into
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			String captchaId = challengeId;
			// call the ImageCaptchaService getChallenge method
			BufferedImage challenge = imageCaptchaService.getImageChallengeForID(captchaId);
			// create the image
			ImageIO.write(challenge, IMAGE_TYPE_JPG, jpegOutputStream);
			captchaByte = jpegOutputStream.toByteArray();
			return captchaByte;
		} catch (IOException e) {
			throw new RuntimeException("HttpServletResponse not found lors de la génèration de captcha");
		}
	}
	/**
	 * Generate Sound captcha
	 * 
	 * @param challengeId the challengeId, captcha's ID
	 * @return a byte array representing the captcha sound
	 * @throws RuntimeException if error occurs
	 */
	private byte[] generateSoundCaptchaAsBytes(String challengeId) throws RuntimeException {
		byte[] captchaChallengeAsAudio = null;
		String captchaId = challengeId;
		AudioInputStream challangeAudio = soundCaptchaService.getSoundChallengeForID(captchaId);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			AudioSystem.write(challangeAudio, javax.sound.sampled.AudioFileFormat.Type.WAVE, byteOutputStream);
			captchaChallengeAsAudio = byteOutputStream.toByteArray();
			return captchaChallengeAsAudio;
		} catch (IOException ioe) {
			LOGGER.error("error generating Audio captcha" + ioe.getMessage(), ioe);
			throw new RuntimeException("error generating Audio captcha");
		}

	}

	/**
	 * Private method to write response to client in the httpResponse
	 * @param httpServletResponse the http response to write for
	 * @param contentType the content type (sound or image)
	 * @param content the byte content
	 */
	private void writeHttpServletResponse(HttpServletResponse httpServletResponse, String contentType, byte[] content) {
		ServletOutputStream responseOutputStream = null;
		try {
			httpServletResponse.setHeader("Cache-Control", "no-store");
			httpServletResponse.setHeader("Pragma", "no-cache");
			httpServletResponse.setDateHeader("Expires", 0);
			httpServletResponse.setContentType(contentType);
			httpServletResponse.setHeader("Content-Length", String.valueOf(content.length));

			
			responseOutputStream = httpServletResponse.getOutputStream();
			responseOutputStream.write(content);
			responseOutputStream.flush();
			responseOutputStream.close();

		} catch (IOException e) {
			throw new RuntimeException("Exception lors de l'ecriture du catpcha dans la reponse http.", e);
		} finally {
			if (responseOutputStream != null) {
				try {
					responseOutputStream.close();
				} catch (IOException ioe) {
					// exception digging
				}
			}
		}

	}
	/**
	 * Setter method for imageCaptchaService (autowired by spring)
	 * @param imageCaptchaService the imageCaptchaService
	 */
	public void setImageCaptchaService(GenericManageableCaptchaService imageCaptchaService) {
		this.imageCaptchaService = imageCaptchaService;
	}

	/**
	 * Setter method for soundCaptchaService (autowired by spring)
	 * @param soundCaptchaService the soundCaptchaService
	 */
	public void setSoundCaptchaService(GenericManageableCaptchaService soundCaptchaService) {
		this.soundCaptchaService = soundCaptchaService;
	}

}
