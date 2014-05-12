package OpenstaackAndJclods.deployer;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class Deployer {

	public static void main(String[] args) {
		
		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

	    Properties overrides = new Properties();
	   
	    String provider = "openstack-nova";
	    String identity =Configuration.KEYSTONE_USERNAME+":"+Configuration.TENANT_NAME;// ExamplesConfiguration.KEYSTONE_USERNAME;
	    String credential = "openstack";//ExamplesConfiguration.KEYSTONE_PASSWORD;
	    String groupName = "default";
	    
	    
	    
	   //Connection to Compute Service 
	    ComputeServiceContext context = ContextBuilder.newBuilder("openstack-nova")
	    		.endpoint(Configuration.NOVA_ENDPOINT)
	    		 .credentials(identity, credential)
	            .modules(modules)
	            .overrides(overrides)
                .buildView(ComputeServiceContext.class);

	    
	    
	    ComputeService computeService = context.getComputeService();
	    org.jclouds.compute.domain.Image img = computeService.getImage("RegionOne/7402865e-9c64-409d-b977-2d760788b321");
	    System.out.println(img.getId());

	    Set<? extends org.jclouds.compute.domain.Image> imgs = computeService.listImages();
	    for(org.jclouds.compute.domain.Image i:imgs){
	    	System.out.println(i.getId());
	    }
	     
	      //NovaClient client = context.getApi();
	      
	    
	    TemplateBuilder tb = computeService.templateBuilder();
	    tb.minRam(Integer.parseInt("512"));
	    tb.imageId("RegionOne/f72885a5-e681-4219-b931-bb062d74c591");
	    NodeMetadata node1=null;
		try {
			node1 = getOnlyElement(computeService.createNodesInGroup(groupName, 1, tb.build()));
		} catch (RunNodesException e) {
		
			e.printStackTrace();
		}
	    System.out.printf("<< node %s: %s%n", node1.getId(),
                concat(node1.getPrivateAddresses(), node1.getPublicAddresses()));
	    
	    
	    Set<? extends ComputeMetadata> cc=computeService.listNodes();
	    for (ComputeMetadata node : cc) {
	    	   System.out.println("\n\n\n\n\nMACHINE:"+node.getId());
	    	   System.out.println(node.getProviderId()); 
	    	   System.out.println(node.getName()); 
	    	   System.out.println(node.getLocation()); 
	    	   NodeMetadata metadata = computeService.getNodeMetadata(node.getId());
	    	   System.out.println(metadata.getId());
	    	   System.out.println( metadata.getProviderId());
	    	   System.out.println(metadata.getLocation());
	    	   System.out.println(metadata.getName());
	    	   System.out.println(metadata.getGroup());
	    	   System.out.println( metadata.getHardware());
	    	   System.out.println( metadata.getImageId()); 
	    	   System.out.println(metadata.getOperatingSystem());
	    	   System.out.println("CREDENTIAL:"+metadata.getCredentials());
	    	   System.out.println(metadata.getPrivateAddresses());
	    	   System.out.println(metadata.getPublicAddresses());
	    	   System.out.println(metadata.getCredentials());
	    	}
	  
	}

}
