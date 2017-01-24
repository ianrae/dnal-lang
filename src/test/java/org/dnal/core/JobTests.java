package org.dnal.core;
//package org.dval;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.dval.csv.CSVLoader;
//import org.dval.csv.CSVParser;
//import org.dval.fluent.type.TypeBuilder;
//import org.dval.logger.Log;
//import org.dval.nrule.ValidationScorer;
//import org.dval.repository.MockRepository;
//import org.dval.util.StringTrail;
//import org.junit.Test;
//
//public class JobTests extends BaseDValTest {
//
//	public static class JobModel {
//		private String id;
//		private String path;
//		private boolean valid;
//		
//		public JobModel(String id, String path) {
//			super();
//			this.id = id;
//			this.path = path;
//			this.valid = false;
//		}
//		
//		public String getId() {
//			return id;
//		}
//		public void setId(String id) {
//			this.id = id;
//		}
//		public String getPath() {
//			return path;
//		}
//		public void setPath(String path) {
//			this.path = path;
//		}
//		public boolean isValid() {
//			return valid;
//		}
//		public void setValid(boolean valid) {
//			this.valid = valid;
//		}
//	}
//	
//	public interface JobStep {
//		void setErrorList(List<ValidationError> list);
//		void execute();
//	}
//	public static class Job {
//		private JobModel job;
//		private List<JobStep> stepList = new ArrayList<>();
//		private List<ValidationError> errorL = new ArrayList<>();
//		private int currentIndex;
//		private boolean haveStarted;
//		private boolean haveFinished;
//		private DTypeRegistry registry;
//		private DStructType type;
//		
//		
//		public Job(JobModel job, DTypeRegistry registry, DStructType type) {
//			this.job = job;
//			this.registry = registry;
//			this.type = type;
//		}
//		public void addStep(JobStep step) {
//			step.setErrorList(errorL);
//			stepList.add(step);
//		}
//		
//		public void start() {
//			haveStarted = true;
//			currentIndex = 0;
//			if (stepList.isEmpty()) {
//				finish();
//				return;
//			}
//
//			doExec();
//		}
//		public boolean isNext() {
//			return isNext(currentIndex);
//		}
//		private boolean isNext(int i) {
//			return (i < (stepList.size() - 1));
//		}
//		public void execNext() {
//			currentIndex++;
//			doExec();
//		}
//		
//		private void doExec() {
//			JobStep step = stepList.get(currentIndex);
//			step.execute();
//			
//			if (! isNext(currentIndex)) {
//				finish();
//			}
//		}
//		
//		private void finish() {
//			haveFinished = true;
//		}
//		
//		public boolean haveStarted() {
//			return haveStarted;
//		}
//		public boolean haveFinished() {
//			return haveFinished;
//		}
//		public List<ValidationError> getErrorL() {
//			return errorL;
//		}
//		
//		public JobStep getIthStep(int index ) {
//			return stepList.get(index);
//		}
//		public DTypeRegistry getRegistry() {
//			return registry;
//		}
//		public DStructType getType() {
//			return type;
//		}
//	}
//	
//	public static class StepBase implements JobStep {
//		protected List<ValidationError> errorL = new ArrayList<>();
//
//		@Override
//		public void execute() {
//			Log.log("step1");
//		}
//
//		@Override
//		public void setErrorList(List<ValidationError> list) {
//			this.errorL = list;
//		}
//	}
//	public static class Step1 extends StepBase {
//
//		@Override
//		public void execute() {
//			Log.log("step1");
//		}
//	}
//	
//	public static class LoadCSVFileStep extends StepBase {
//		private String path;
//		private DTypeRegistry registry;
//		private CSVParser parser;
//		private DStructType type;
//		private Job job;
//		
//		public LoadCSVFileStep(String path, Job job) {
//			this.path = path;
//			this.job = job;
//			this.registry = job.getRegistry();
//			parser = new CSVParser(registry);
//			this.type = job.getType();
//		}
//		
//		@Override
//		public void execute() {
//			CSVLoader loader = new CSVLoader(path);
//			if (! loader.open()) {
//				ValidationError err = new ValidationError(ValidationErrorType.PARSINGERROR, String.format("can't open '%s'", path));
//				this.errorL.add(err);
//				return;
//			}
//
//			String [] nextLine;
//			boolean seenFirstYet = false;
//			while ((nextLine = loader.readLine()) != null) {
//				if (! seenFirstYet) {
//					seenFirstYet = true;
//					String hdr = new StringTrail(loader.getHdr()).toString();
//					parser.setupType(type, hdr);
//				} else {
//					doLine(nextLine);
//				}
//			}	
//			doEnd();
//			
//			StringTrail trail = new StringTrail(loader.getHdr());
//			Log.log("hdr: " + trail.toString());
//		}
//
//		private void doLine(String[] nextLine) {
//			String line = new StringTrail(nextLine).toString(); //;
//			parser.parseLine(line);
//		}
//		
//		private void doEnd() {
//			parser.validate();
//			this.errorL.addAll(parser.getValidationErrors());
//		}
//		
//		private List<DValue> getRawData() {
//			return parser.getDvalList();
//		}
//		
//		private List<DValue> getValidatedData() {
//			if (parser.wasSuccessful()) {
//				List<DValue> validL = new ArrayList<>();
//				for(DValue dval: parser.getDvalList()) {
//					if (dval.isValid()) {
//						validL.add(dval);
//					}
//				}
//				return validL;
//			} else {
//				return null;
//			}
//		}
//
//		public CSVParser getParser() {
//			return parser;
//		}
//	}
//	
//	@Test
//	public void testEmpty() {
//		String path = "src/main/resources/test/products1.csv";
//		JobModel jobModel = createJobModel(path);
//		Job job = createJob(jobModel, 0, path);
//		assertEquals(false, job.haveStarted());
//		assertEquals(false, job.haveFinished());
//		
//		job.start();
//		chkRunning(job, true, false);
//		
//		assertEquals(true, job.haveStarted());
//		assertEquals(true, job.haveFinished());
//		assertEquals(false, job.isNext());
//	}
//	
//	@Test
//	public void testOne() {
//		String path = "src/main/resources/test/products1.csv";
//		JobModel jobModel = createJobModel(path);
//		Job job = createJob(jobModel, 1, path);
//		assertEquals(false, job.haveStarted());
//		assertEquals(false, job.haveFinished());
//		
//		job.start();
//		chkRunning(job, true, false);
//	}
//	
//	@Test
//	public void testTwo() {
//		String path = "src/main/resources/test/products1.csv";
//		JobModel jobModel = createJobModel(path);
//		Job job = createJob(jobModel, 2, path);
//		assertEquals(false, job.haveStarted());
//		assertEquals(false, job.haveFinished());
//		
//		job.start();
//		chkRunning(job, true, true);
//
//		job.execNext();
//		chkRunning(job, true, false);
//		
//		LoadCSVFileStep step = (LoadCSVFileStep) job.getIthStep(1);
//		List<DValue> rawList = step.getRawData();
//		assertEquals(3, rawList.size());
//		List<DValue> list = step.getValidatedData();
//		assertEquals(3, list.size());
//		
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, true, false, false);
//		registry.dump();
//	}
//	
//	@Test
//	public void testTwoBad() {
//		String path = "src/main/resources/test/productsbad1.csv";
//		JobModel jobModel = createJobModel(path);
//		Job job = createJob(jobModel, 2, path);
//		assertEquals(false, job.haveStarted());
//		assertEquals(false, job.haveFinished());
//		
//		job.start();
//		chkRunning(job, true, true);
//
//		job.execNext();
//		chkRunning(job, true, false);
//		
//		LoadCSVFileStep step = (LoadCSVFileStep) job.getIthStep(1);
//		List<DValue> rawList = step.getRawData();
//		assertEquals(1, rawList.size());
//		List<DValue> list = step.getValidatedData();
//		assertNull(list);
//		
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, true, false, false);
//		registry.dump();
//		this.dumpValErrors(step.getParser().getValidationErrors());
//	}
//	
//	
//	//--
//	private void chkRunning(Job job, boolean running, boolean isMore) {
//		assertEquals(true, job.haveStarted());
//		assertEquals(! isMore, job.haveFinished());
//		assertEquals(isMore, job.isNext());
//	}
//	
//	private DStructType buildProductType(DTypeRegistry registry) {
//		TypeBuilder tb = new TypeBuilder(registry);
//		tb.start("Product")
//		.string("code").minSize(3)
//		.string("desc")
//		.integer("age")
//		.end();
//
//		DStructType type = tb.getType();
//		return type;
//	}
//	private JobModel createJobModel(String path) {
//		JobModel jobmodel = new JobModel("100", path);
//		return jobmodel;
//	}
//
//	private Job createJob(JobModel jobModel, int num, String path) {
//		DStructType type = buildProductType(registry);
//		Job job = new Job(jobModel, registry, type);
//		if (num == 0) {
//			return job;
//		}
//		
//		job.addStep(new Step1());
//		if (num == 1) {
//			return job;
//		}
//		
//		job.addStep(new LoadCSVFileStep(path, job));
//		if (num == 2) {
//			return job;
//		}
//		
//		return job;
//	}
//}
