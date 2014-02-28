package fr.mcc.transverse.captcha.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface CaptchaWebService {
	/**
	 * Web service to check captcha
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @param type the captcha type (enum of type CaptchaType : IMAGE or SOUND)
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkCaptcha(@WebParam(name="challengeId") String challengeId, @WebParam(name="userResponse") String userResponse, @WebParam(name="captchaType") CaptchaType type);
	/**
	 * Web service to check captcha image
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkImageCaptcha(@WebParam(name="challengeId") String challengeId, @WebParam(name="userResponse") String userResponse);
	/**
	 * Web service to check captcha sound
	 * @param challengeId the unique captcha id
	 * @param userResponse the user response
	 * @return true only if captcha is valid
	 */
	@WebMethod
	public Boolean checkSoundCaptcha(@WebParam(name="challengeId") String challengeId, @WebParam(name="userResponse") String userResponse);
	/**
	 * Web service to generate a unique id used for captcha
	 * @return a unique id as string
	 */
	@WebMethod
	public String generateUniqueChallengeID();
}
