package test;

import java.util.ArrayList;
import org.jpl7.Term;
import org.jpl7.Util;
import casp.Settings;


public class TestExpRuleDigestToExpectationObject {

	public static void main(String[] args) {
		Term t = Util.textToTerm("exp_rule(happ(join(skolem, fishingteam, fishingteam_watcherrole)), next(next(always(happ(fishWithTeam(skolem))))))");
		//System.out.println(t);
		Expectation ex = ExpectationFactory.getExpectation(t);
		System.out.println(ex);
		
	}
	

}

class Expectation {
	public String exp_type = "";
	public String exp_rule_event_name = "";
	public  ArrayList<String> exp_rule_event_parameters = new ArrayList<String> ();
	public String eventually_event_name = "";
	public  ArrayList<String> eventually_event_parameters =  new ArrayList<String> ();
	public String temporal_modality = "expected";
	
	Expectation(){}//package visibility
	
	public String toString(){
		return key();
	}
	
	public String key(){
		return exp_type+" "+exp_rule_event_name + Settings.listString(exp_rule_event_parameters.toString()) +
				" " + temporal_modality + ": "+eventually_event_name +Settings.listString(eventually_event_parameters.toString());
	}
	

}


class ExpectationFactory {

	
	private static ExpectationFactory builder = new ExpectationFactory();
	private ExpectationFactory(){}
	
	
	/**
	 * Get an Expectation instance based on the passed Term.
	 * @param t the Term
	 * @return a new Expectation instance
	 */
	public static Expectation getExpectation(Term t){
		builder.donerule = false;
		builder.doneeven = false;
		Expectation expt = new Expectation();
		
		expt.exp_type = t.name();
		builder.recurseTerm(t,1,expt);
		return expt;

	}
	
	private void gatherTerms(Term t, ArrayList<String> parameters){
		Term[] argz = t.args();
		for (int i = 0; i < argz.length; i++){
			parameters.add(argz[i].name());
		}
	}

	private boolean donerule = false;
	private boolean doneeven = false;
	
	private void recurseTerm(Term t, int depth, Expectation expt){
		if (t.isVariable())return;
		if (t.isInteger())return;
		String name = t.name();

		if (depth == 1 ){
			
		} else if (name.equals("happ")) {
		   
			if (depth == 2&&!donerule){
	
				expt.exp_rule_event_name = t.arg(1).name();
	
				gatherTerms(t.arg(1),expt.exp_rule_event_parameters);
				donerule = true;
	
			} else if (depth >= 3&&!doneeven){
				expt.eventually_event_name =t.arg(1).name();
				gatherTerms(t.arg(1),expt.eventually_event_parameters);
				doneeven = true;
			}
		} else if (name.equals("always") || name.equals("eventually") ) {
			expt.temporal_modality = name;
		}
	
		Term[] argz = null;
		try {
			argz = t.args();
		} catch (Exception e) {
	
			System.out.println(e.getMessage());
			return;
		}
		//whatIsIt(t,getDepthString(depth));
		for (int i = 0; i < argz.length; i++){
	
			Term tx = argz[i];
			
	
			recurseTerm(tx,depth+1, expt);
			
		}
	}

}
