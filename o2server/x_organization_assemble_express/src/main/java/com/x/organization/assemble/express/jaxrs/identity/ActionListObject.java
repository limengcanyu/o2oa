package com.x.organization.assemble.express.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;

class ActionListObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getIdentityList());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Identity {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		for (String str : wi.getIdentityList()) {
			Identity o = business.identity().pick(str);
			if(o!=null){
				Wo wo = this.convert(business, o, Wo.class);
				wo.setMatchKey(str);
				wos.add(wo);
			}
		}
		return wos;
	}

}