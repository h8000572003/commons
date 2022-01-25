package io.github.h800572003.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.github.h800572003.exception.ApBusinessException;

/**
 * 快取取號
 * 
 * @author
 *
 */
public class GetNoServiceInMem implements IGetNoService {


	private LoadingCache<String, AtomicInteger> loadingCache;

	public GetNoServiceInMem(GetNoServiceCofog cofog) {
		this.loadingCache = CacheBuilder.newBuilder().maximumSize(cofog.cacheSize)//
				.expireAfterAccess(cofog.expireAfterAccess, TimeUnit.SECONDS)//
				.build(new CacheLoader<String, AtomicInteger>() {

					@Override
					public AtomicInteger load(String key) throws Exception {
						return new AtomicInteger();
					}

				});
	}

	public GetNoServiceInMem() {
		this(new GetNoServiceCofog());
	}

	@Override
	public synchronized String getNo(String key, int size) {
		String format = key + "%0" + size + "d";
		try {
			AtomicInteger i = loadingCache.get(key);
			return String.format(format, i.incrementAndGet());
		} catch (ExecutionException e) {
			return String.format(format, 0);
		}

	}

	static class GetNoServiceCofog {
		int expireAfterAccess = 60;
		int cacheSize = 1000;
		int initNo = 0;

		public void setExpireAfterAccess(int expireAfterAccess) {
			if (expireAfterAccess <= 0) {
				throw new ApBusinessException("must be greater than 0");
			}
			this.expireAfterAccess = expireAfterAccess;
		}

		public void setCacheSize(int cacheSize) {
			if (expireAfterAccess <= 0) {
				throw new ApBusinessException("must be greater than 0");
			}
			this.cacheSize = cacheSize;
		}

		public void setInitNo(int initNo) {
			if (expireAfterAccess <= 0) {
				throw new ApBusinessException("must be greater than 0");
			}
			this.initNo = initNo;
		}

	}

}
