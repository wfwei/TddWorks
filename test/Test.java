import org.apache.log4j.Logger;



public class Test {
	private static final Logger LOG = Logger.getLogger(Test.class);
	public static void main(String args[]){
		//PropertyConfigurator.configure("resources/log4j.properties");
		System.out.println("here");
		LOG.info("warn you");
	}

}
