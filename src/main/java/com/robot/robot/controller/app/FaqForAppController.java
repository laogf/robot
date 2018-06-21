package com.robot.robot.controller.app;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.robot.common.utils.RequestUtil;
import com.robot.common.utils.TuLingUtils;
import com.robot.common.utils.XFyunUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;
import com.robot.common.domain.TuLingReturn;
import com.robot.common.domain.XFyunReturn;
import com.robot.robot.controller.app.bean.FaqRequestBean;
import com.robot.robot.controller.app.bean.ResponseBean;
import com.robot.robot.controller.app.common.AppConstants;
import com.robot.robot.domain.TFaqDO;
import com.robot.robot.domain.TKeywordDO;
import com.robot.robot.domain.TKeywordMiddleDO;
import com.robot.robot.domain.TKeywordReplaceDO;
import com.robot.robot.domain.TNonexistentFaqDO;
import com.robot.robot.service.TFaqService;
import com.robot.robot.service.TKeywordMiddleService;
import com.robot.robot.service.TKeywordReplaceService;
import com.robot.robot.service.TKeywordService;
import com.robot.robot.service.TNonexistentFaqService;
import com.robot.robot.utils.SessionUtil;


@Controller
@RequestMapping("/app/faq")
public class FaqForAppController {
	public static Logger log = Logger.getLogger(FaqForAppController.class); 
	
	@Autowired
	private TFaqService tFaqService;
	@Autowired
	private TKeywordService tKeywordService;
	@Autowired
	private TNonexistentFaqService tNonexistentFaqService;
	@Autowired
	private TKeywordMiddleService tKeywordMiddleService;
	@Autowired
	private TKeywordReplaceService tKeywordReplaceService;

	/**
	 * 智能查询问答
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/searchAnswer")
	@ResponseBody
	public ResponseBean searchAnswer(HttpServletRequest request) throws Exception{
		String content=RequestUtil.getString(request, "content");
		String robotNo=RequestUtil.getString(request, "robotNo");

		//退出即清空session缓存
		if(content.equals(AppConstants.OUT)){
			SessionUtil.removeSessionAttribute(robotNo);
		}
		String parentId  = SessionUtil.getAttr(robotNo);		//获取当前机械人语音是否有上级问答
		log.info("语音回答上级 parentId:"+parentId);
		
		log.info("content:"+content +",robotNo:"+robotNo);
		
		if(StringUtils.isEmpty(content)){
			return ResponseBean.fail("参数格式不对");
		}
		
		ResponseBean res =new ResponseBean();
		if(StringUtils.isNotEmpty(parentId)){
			int countParent=tFaqService.countParent(parentId);
			if(countParent>0){
				//大于0即代表有下级问答
				res =parentSearch(content,robotNo,parentId);
			}else{
				//0即代表没有下级问答
				res =commonSearch(content,robotNo);
			}
		}else{
			res =commonSearch(content,robotNo);
		}
		return res;
		
		
		/***********************注释为原回答逻辑*******************************/
//		String content=RequestUtil.getString(request, "content");
//		log.info("content:"+content);
//		
//		if(StringUtils.isEmpty(content)){
//			return ResponseBean.fail("参数格式不对");
//		}
//		//优先通过问答查询答案
//		TFaqDO tFaq=new TFaqDO();
//		tFaq=tFaqService.getLikeByQuestion(content);
//		if(tFaq.getFaqId() != null){
//			FaqRequestBean faqBean=new FaqRequestBean();
//			faqBean.setQuestion(tFaq.getQuestion());
//			faqBean.setAnswer(tFaq.getAnswer());
//			
//			return ResponseBean.success(faqBean);
//		}	
//		
//		//关键字查询
//		String gc=tKeywordService.strReplace(content);
//		long faqId=tFaqService.searchFaqForApp(gc);
//		if(faqId!=0){//通过关键字查询答案
//			tFaq=tFaqService.get(faqId);
//			
//			FaqRequestBean faqBean=new FaqRequestBean();
//			faqBean.setQuestion(content);
//			faqBean.setAnswer(tFaq.getAnswer());
//			
//			return ResponseBean.success(faqBean);
//		}else{
////			tFaq=tFaqService.getLikeByQuestion(content);
////			if(tFaq.getFaqId() != null){
////				FaqRequestBean faqBean=new FaqRequestBean();
////				faqBean.setQuestion(tFaq.getQuestion());
////				faqBean.setAnswer(tFaq.getAnswer());
////				
////				return ResponseBean.success(faqBean);
////				
////			}else{
//				//查询不到答案记录问题
//				TuLingReturn tuLingReturn = TuLingUtils.postTalk(content);
//				String result = tuLingReturn.getText();
//				log.info("未找到答案,通过图灵寻找 question:"+content+",result:"+result);
//				
//				FaqRequestBean faqBean=new FaqRequestBean();
//				faqBean.setQuestion(content);
//				faqBean.setAnswer(result);
//				
//				if(tNonexistentFaqService.existQuestion(gc)){
//					TNonexistentFaqDO tNonexistentFaq=new TNonexistentFaqDO();
//					tNonexistentFaq.setQuestion(gc);
//					tNonexistentFaq.setAnswer(result);
//					tNonexistentFaq.setCreatetime(new Date());
//					tNonexistentFaqService.save(tNonexistentFaq);
//				}
//				
//				return ResponseBean.success(faqBean);
////			}
//			
//		}
		
	}

	
	public ResponseBean commonSearch(String content,String robotNo) throws Exception{
		
		FaqRequestBean faqBean=new FaqRequestBean();
		
		//优先通过问答查询答案
		TFaqDO tFaq=new TFaqDO();
		tFaq=tFaqService.getLikeByQuestion(content);
		if(tFaq.getFaqId() != null){
			faqBean.setQuestion(tFaq.getQuestion());
			faqBean.setAnswer(tFaq.getAnswer());
			
			SessionUtil.setRequestAttribute(robotNo, String.valueOf(tFaq.getFaqId()));
			return ResponseBean.success(faqBean);
		}	
		faqBean = getAnswer(content,tFaq,robotNo);
		return ResponseBean.success(faqBean);
	}
	
	public ResponseBean parentSearch(String content,String robotNo,String parentId) throws Exception{
		
		FaqRequestBean faqBean=new FaqRequestBean();
		
		//优先通过问答查询答案
		TFaqDO tFaq=new TFaqDO();
		tFaq=tFaqService.getLikeByQuestionForParent(content, parentId);
		if(tFaq.getFaqId() != null){
			faqBean.setQuestion(tFaq.getQuestion());
			faqBean.setAnswer(tFaq.getAnswer());
			
			SessionUtil.setRequestAttribute(robotNo, String.valueOf(tFaq.getFaqId()));
			return ResponseBean.success(faqBean);
		}
		faqBean = getAnswer(content,tFaq,robotNo);
		return ResponseBean.success(faqBean);
	}
	
	/**
	 * 
	* @Functionlity  公共问题查找答案（关键字==>讯飞==>图灵）
	* @Date  2018年6月14日
	* @param content
	* @return FaqRequestBean
	 * @throws Exception 
	 */
	public FaqRequestBean getAnswer(String content,TFaqDO tFaq,String robotNo) throws Exception{
		TNonexistentFaqDO tNonexistentFaq=new TNonexistentFaqDO();
		FaqRequestBean faqBean=new FaqRequestBean();
		
		//关键字查询
		String gc=tKeywordService.strReplace(content);
		//long faqId=tFaqService.searchFaqForApp(gc);
		long faqId=0;
		
		if(faqId!=0){//通过关键字查询答案
			tFaq=tFaqService.get(faqId);
			
			faqBean.setQuestion(content);
			faqBean.setAnswer(tFaq.getAnswer());
			
			SessionUtil.setRequestAttribute(robotNo, String.valueOf(tFaq.getFaqId()));
			return faqBean;
		}else{
			//全文检索找不到答案，按照用户意图给出问题提示
			String jbAnswer = jieBaFenCi(content);
			if(jbAnswer!=null){
				faqBean.setQuestion(content);
				faqBean.setAnswer(jbAnswer);
				return faqBean;
			}
			
			//本地知识库查询不到答案转<讯飞>记录问题
			XFyunReturn xfReturn = XFyunUtils.runChat(content);
			String result = xfReturn.getText();
			if(result==null || result==""){
				log.info("==========未找到答案,通过<讯飞>寻找 question:"+content+",result:"+result);
				faqBean.setQuestion(content);
				faqBean.setAnswer(result);
				
				if(tNonexistentFaqService.existQuestion(gc)){
					tNonexistentFaq.setQuestion(gc);
					tNonexistentFaq.setAnswer(result);
					tNonexistentFaq.setCreatetime(new Date());
					tNonexistentFaqService.save(tNonexistentFaq);
				}
				return faqBean;
			}
			//<讯飞>+知识库不存在关键字，查询不到答案转<图灵>记录问题
			TuLingReturn tuLingReturn = TuLingUtils.postTalk(content);
			//System.out.println(tuLingReturn.getCode()+"===="+tuLingReturn.getText());
			//String TLresult = tuLingReturn.getText();
			String TLresult = tuLingReturn.getText();
			log.info("==========未找到答案,通过<图灵>寻找 question:"+content+",result:"+TLresult);
			faqBean.setQuestion(content);
			faqBean.setAnswer(TLresult);
			
			//保存知识库没有的问答
			if(tNonexistentFaqService.existQuestion(gc)){
				tNonexistentFaq.setQuestion(gc);
				tNonexistentFaq.setAnswer(TLresult);
				tNonexistentFaq.setCreatetime(new Date());
				tNonexistentFaqService.save(tNonexistentFaq);
			}
			return faqBean;
		}
	}
	
	
	/**
	 * 结巴分词查询答案，没有则返回问题
	 */
	public String jieBaFenCi(String content){
		TFaqDO tFaqDO = null;
		//结巴分词提取关键字
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> segToken = segmenter.process(content, SegMode.INDEX);
		
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		Map<String,Integer> map3 = new HashMap<String,Integer>();
		List<TKeywordMiddleDO> kwM = tKeywordMiddleService.list(map2);
		//初始化map3匹配数组
		for(int k=0; k<kwM.size(); k++){
			String faqID = kwM.get(k).getFaqId().toString();
			map3.put(faqID, 0);
		}
		
		double natchCount = 0;//关键字匹配个数
		
		for (int s=0; s<segToken.size(); s++ ){
			Long kwID = null;
			
			String jieBaWord = segToken.get(s).word;
			map.put("name", jieBaWord);
			List<TKeywordDO> kwList = tKeywordService.list(map);
			//关键字替换查找
			if(kwList.size() <= 0){
				Map<String,Object> getReplaceByCharKey = new HashMap<String,Object>();
				getReplaceByCharKey.put("charkey", jieBaWord);
				List<TKeywordReplaceDO> kwRep = tKeywordReplaceService.list(getReplaceByCharKey);
				//找到替换关键字
				if(kwRep.size()>0){
					for (TKeywordReplaceDO kwr : kwRep){
						kwID = kwr.getKeywordId();
					}
				}
				
			}else if(kwList.size()>0){
				for (TKeywordDO kw : kwList){
					kwID = kw.getKeywordId();
				}
			}
			
			//关键字存在则查看关联问题
			if(kwID != null){
				
				for(int k=0; k<kwM.size(); k++){
					String []  kwmArray = kwM.get(k).getKeygroup().split(",");
					String faqID = kwM.get(k).getFaqId().toString();
					
					for (int i=0; i<kwmArray.length; i++){
						//Long kwmID = Long.parseLong(kwmArray[i]);
						String kwmID = kwmArray[i];
						if (kwID.toString().equals(kwmID)){
							int m = map3.get(faqID);
							if(m==0 || m>0){
								map3.put(faqID, m+1);
							}
						}
					}
				}
			}
			
		}

		Map<String,Double> map4 = new HashMap<String,Double>();
		for (String temp : map3.keySet()) {
	            double value = map3.get(temp);
	            //判断分词关键字是否存在，如果存在则存入与关键字组合的占比
	            if(value>0){
	            	int kwmc= tFaqService.get(Long.parseLong(temp)).getKeygroup().split(",").length;
	            	double v = value/kwmc;
	            	map4.put(temp, v);
	            }
	            
	    }

		if(map4.size()>0){
			String lastFaqIDstring = getKeyByMaxValue(map4);
			Long lastFaqID = Long.parseLong(lastFaqIDstring);
			tFaqDO = tFaqService.get(lastFaqID);
			natchCount = map4.get(lastFaqIDstring);
		}

		//如果问题中关键字个数与匹配个数一样证明完全匹配问题可以给出答案，否则给出问题提示
		if(natchCount==1){
			return tFaqDO.getAnswer();
		}else if(natchCount==0){
			return null;
		}else{
			return "你可以这样问："+tFaqDO.getQuestion();
		}
	}
	
	/**
	 * 取double最大值
	 */
	public String getKeyByMaxValue(Map<String,Double> map){
		List<Double> list = new ArrayList<Double>();
	    String returnKey = "";
        for (String temp : map.keySet()) {
            double value = map.get(temp);
            list.add(value);
        }
        double max = 0;
        for (int i = 0; i < list.size(); i++) {
            double size = list.get(i);
            max = (max>size)?max:size;
        }
        for (String key : map.keySet()) {
            if (max == map.get(key)) {
            	returnKey =  key;
            }
        }
        return returnKey;
	}
	
}
