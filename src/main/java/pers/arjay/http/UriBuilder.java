package pers.arjay.http;

import java.util.Iterator;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * compile group: 'org.springframework.data', name: 'spring-data-commons', version: '2.0.9.RELEASE'
 * 
 * @author jay.kuo
 *
 */
public class UriBuilder {

	/**
	 * 產生分頁設定的URL
	 * 
	 * @param domain
	 * @param path
	 * @param pageable
	 * @param defaultSortColumn
	 * @return
	 */
	public static String build(String domain, String path, Pageable pageable, String defaultSortColumn) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(domain + path);

		if (pageable != null) {
			builder.queryParam("page", pageable.getPageNumber());
			builder.queryParam("size", pageable.getPageSize());

			if (pageable.getSort() == null) {
				builder.queryParam("sort", defaultSortColumn + "," + Direction.DESC);
			} else {
				Iterator<Order> orders = pageable.getSort().iterator();
				while (orders.hasNext()) {
					Order order = orders.next();
					builder.queryParam("sort", order.getProperty() + "," + order.getDirection());
				}
			}
		}
		
		return builder.build().toString();
	}

}
