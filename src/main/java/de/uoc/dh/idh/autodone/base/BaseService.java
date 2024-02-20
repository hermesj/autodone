package de.uoc.dh.idh.autodone.base;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_PAGINATION;
import static java.util.Collections.emptyList;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.compare;
import static java.util.UUID.fromString;
import static org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public abstract class BaseService<T> {

	public abstract void delete(UUID uuid, String username, String domain);

	public abstract T getOne(UUID uuid, String username, String domain);

	public abstract Page<T> getPage(Pageable request, String username, String domain);

	//

	protected abstract Sort getSort();

	//

	public void delete(String uuid) {
		delete(uuid, (OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public void delete(String uuid, OAuth2AuthenticationToken oauth) {
		delete(fromString(uuid), oauth.getName(), oauth.getAuthorizedClientRegistrationId());
	}

	//

	public T getOne(String uuid) {
		return getOne(uuid, (OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public T getOne(String uuid, OAuth2AuthenticationToken oauth) {
		return getOne(fromString(uuid), oauth.getName(), oauth.getAuthorizedClientRegistrationId());
	}

	//

	public Page<T> getPage(String page, String sort) {
		return getPage(pageRequest(page, sort), (OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public Page<T> getPage(Pageable request, OAuth2AuthenticationToken oauth) {
		return getPage(request, oauth.getName(), oauth.getAuthorizedClientRegistrationId());
	}

	//

	@SuppressWarnings("all")
	protected Page<T> getPage(PageRequest request, List<T> entities) {
		int offset = request.getPageNumber() * request.getPageSize();
		int length = Math.min(offset + request.getPageSize(), entities.size());

		if (request.getSort().isSorted()) {
			try {
				entities.sort((one, two) -> {
					var wrapperOne = forBeanPropertyAccess(one);
					var wrapperTwo = forBeanPropertyAccess(two);

					for (var order : request.getSort()) {
						var direction = order.isAscending() ? naturalOrder() : reverseOrder();
						var valueOne = wrapperOne.getPropertyValue(order.getProperty());
						var valueTwo = wrapperTwo.getPropertyValue(order.getProperty());
						var result = compare(valueOne, valueTwo, (Comparator) direction);

						if (result != 0) {
							return result;
						}
					}

					return 0;
				});
			} catch (Exception exception) {
			}
		}

		if (offset > entities.size()) {
			return new PageImpl<>(emptyList(), request, 0);
		} else {
			return new PageImpl<>(entities.subList(offset, length), request, entities.size());
		}
	}

	//

	protected PageRequest pageRequest(String page, String sort) {
		var pageable = page == null ? 0 : Integer.parseInt(page);
		var sortable = sort == null ? getSort() : by(sort);
		return of(pageable, AUTODONE_PAGINATION, sortable);
	}

}
