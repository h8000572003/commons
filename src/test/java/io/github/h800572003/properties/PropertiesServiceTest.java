package io.github.h800572003.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import io.github.h800572003.properties.Code;
import io.github.h800572003.properties.IPropertieServiceHolder;
import io.github.h800572003.properties.IProperties;
import io.github.h800572003.properties.IPropertiesRepository;
import io.github.h800572003.properties.IPropertiesService;
import io.github.h800572003.properties.MyProperties;
import io.github.h800572003.properties.PropertiesHolderBuilder;
import io.github.h800572003.properties.PropertiesService;
import io.github.h800572003.properties.IPropertiesService.PropertiesUpdateListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PropertiesServiceTest {

	private static final String CATEGORY = "CATEGORY";
	private static final String KEY = "KEY";

	private static final String MEMO = "備註A";

	private MyProperties myProperties = new MyProperties();
	private IPropertiesRepository propertiesRepository = new IPropertiesRepository.PropertiesRepositoryInMen();

	private IPropertiesService propertiesService = new PropertiesService(propertiesRepository);
	IPropertieServiceHolder spy = null;

	private PropertiesUpdateListener propertiesUpdateListener = Mockito.spy(new PropertiesUpdateListener() {

		@Override
		public void update(String category, IProperties properties) {
			log.info("updae: {},properties:{}", category, properties);

		}
	});

	public PropertiesServiceTest() {

		propertiesService.addListener(propertiesUpdateListener);

		List<IProperties> newArrayList = Lists.newArrayList();
		myProperties.setKey(KEY);
		myProperties.setMeno(MEMO);
		myProperties.setValue1("A-1");
		myProperties.setValue2("A-2");
		myProperties.setValue3("A-3");
		newArrayList.add(myProperties);

		PropertiesHolderBuilder builder = new PropertiesHolderBuilder(true, true);
		IPropertieServiceHolder holder = builder.setProperties(newArrayList).build(CATEGORY, propertiesRepository);
		spy = Mockito.spy(holder);
		propertiesService.addCode(CATEGORY, KEY, spy);
	}

	/**
	 * 
	 */
	@Test
	void testGetCategoryWhenNotEmpty() {
		Code code = new Code();
		code.setKey(CATEGORY);
		code.setValue(KEY);
		assertThat(propertiesService.getCategory()).contains(code).hasSize(1).isNotEmpty();
	}

	/**
	 * 取得代碼檔案內容，為原始資料
	 */
	@Test
	void testGetValue() {
		assertThat(propertiesService.getValue(CATEGORY, KEY)).describedAs("取得代碼檔案")
				.matches(t -> t.equals(myProperties));
	}

	/**
	 * 更新KEY資料，檢查資料是否變更為新
	 */
	@Test
	void testSaveOrUpdate() {

		MyProperties updateProperties = new MyProperties();
		updateProperties.setKey(KEY);
		updateProperties.setMeno(MEMO);
		updateProperties.setValue1("B-1");
		updateProperties.setValue2("B-2");
		updateProperties.setValue3("B-3");
		propertiesService.saveOrUpdate(CATEGORY, updateProperties);

		IProperties value = propertiesService.getValue(CATEGORY, KEY);
		assertThat(value).describedAs("取得代碼檔案").matches(t -> t.equals(updateProperties));

		Mockito.verify(propertiesUpdateListener, Mockito.times(1)).update(CATEGORY, updateProperties);

	}

	/**
	 * 刪除 1.更新代碼內容 2.執行刪除 3.檢查結果是否為初始值
	 */
	@Test
	void testDelete() {
		MyProperties updateProperties = new MyProperties();
		updateProperties.setKey(KEY);
		updateProperties.setMeno(MEMO);
		updateProperties.setValue1("B-1");
		updateProperties.setValue2("B-2");
		updateProperties.setValue3("B-3");
		this.propertiesService.saveOrUpdate(CATEGORY, updateProperties);
		this.propertiesService.delete(CATEGORY);
		List<IProperties> propertie = propertiesService.getPropertie(CATEGORY);
		assertThat(propertie).hasSize(1);
		assertThat(propertie.get(0)).matches(t -> t.equals(myProperties));
	}

	@Test
	void testClear() {
		this.propertiesService.clear();
		Mockito.verify(spy, Mockito.times(1)).clear();
	}

	@Test
	void testUpdate() {
		this.propertiesService.update();
		Mockito.verify(spy, Mockito.times(1)).clear();
		Mockito.verify(spy, Mockito.times(1)).getProperties();
	}

}
