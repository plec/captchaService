package fr.mcc.transverse.captcha.service;

import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

@WebService(endpointInterface = "fr.mcc.transverse.captcha.service.CaptchaWebService", 
targetNamespace = "http://fr.mcc.transverse.captcha.service/catpcha/v1.0.0", serviceName = "CaptchaService", name = "Captcha")
public class CaptchaWebServiceImpl implements CaptchaWebService {
	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(CaptchaWebServiceImpl.class);
	/**
	 * captcha service for image captcha
	 */
	private GenericManageableCaptchaService imageCaptchaService;
	/**
	 * captcha service for sound captcha
	 */
	private GenericManageableCaptchaService soundCaptchaService;

	/**
	 * Web service to check captcha
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @param type the captcha type (enum of type CaptchaType : IMAGE or SOUND)
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkCaptcha(String challengeId, String userResponse, CaptchaType type) {
		LOGGER.info("check captcha for challenge : " + challengeId + " for type " + type.toString());
		GenericManageableCaptchaService captacheService = null;
		if (type.equals(CaptchaType.IMAGE)) {
			captacheService = imageCaptchaService;
		} else {
			captacheService = soundCaptchaService;
		}
		if (validateChallenge(captacheService, challengeId, userResponse)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Web service to check captcha image
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkImageCaptcha(String challengeId, String userResponse) {
		LOGGER.info("check captcha image for challenge : " + challengeId);
		return checkCaptcha(challengeId, userResponse, CaptchaType.IMAGE);
	}
	/**
	 * Web service to check captcha sound
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkSoundCaptcha( String challengeId, String userResponse) {
		LOGGER.info("check captcha sound for challenge : " + challengeId);
		return checkCaptcha(challengeId, userResponse, CaptchaType.SOUND);
	}
	/**
	 * Web service to generate a unique id used for captcha
	 * @return a unique id as string
	 */
	@WebMethod
	public String generateUniqueChallengeID() {
		LOGGER.info("generate unique captcha ID");
		return UUID.randomUUID().toString();
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
			String userResponseMin = userResponse.toLowerCase();
			LOGGER.info("check captcha for challenge : " + challengeId + " user response : " + userResponseMin);
			if (captacheService.validateResponseForID(challengeId, userResponseMin)) {
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
	 * @param imageCaptchaService the imageCaptchaService to set
	 */
	public void setImageCaptchaService(GenericManageableCaptchaService imageCaptchaService) {
		this.imageCaptchaService = imageCaptchaService;
	}
	/**
	 * @param soundCaptchaService the soundCaptchaService to set
	 */
	public void setSoundCaptchaService(GenericManageableCaptchaService soundCaptchaService) {
		this.soundCaptchaService = soundCaptchaService;
	}
}
