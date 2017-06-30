#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
import org.slf4j.*;
#parse("File Header.java")
public class ${NAME} {
    private static final Logger LOG = LoggerFactory.getLogger(${NAME}.class);
}
