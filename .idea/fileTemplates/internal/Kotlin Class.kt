#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
import mu.KotlinLogging

#parse("File Header.java")
class ${NAME} {
    
    companion object {
    
        val logger = KotlinLogging.logger { }
    
    }
    
    
    
}