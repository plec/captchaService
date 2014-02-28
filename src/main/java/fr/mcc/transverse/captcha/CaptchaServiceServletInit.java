package fr.mcc.transverse.captcha;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.ServletContextAware;

import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class CaptchaServiceServletInit implements ServletContextAware {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(CaptchaServiceServletInit.class);
    
    /** le path des fichiers de configuration */
    private String configPath;
    
    /**
     * initialiser les Properties pour le captcha
     * @throws TechnicalException 
     * @throws TechnicalException exception technique
     */
    public CaptchaServiceServletInit() throws RuntimeException {
    }

    /**
     * Setter method for configPath
     * @param configPath the configPath to set
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void setServletContext(ServletContext servletContext) {
        AppPathService.init(servletContext);
        try {
            AppPropertiesService.init(configPath);
        } catch (LuteceInitException e) {
            LOGGER.error("Impossible d'initialiser le son pour le captcha.", e);
        }
    }


}
