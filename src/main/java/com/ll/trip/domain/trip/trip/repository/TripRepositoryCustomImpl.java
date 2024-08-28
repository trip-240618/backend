package com.ll.trip.domain.trip.trip.repository;

import java.time.LocalDate;
import java.util.List;

import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.entity.QBookmark;
import com.ll.trip.domain.trip.trip.entity.QTrip;
import com.ll.trip.domain.trip.trip.entity.QTripMember;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TripRepositoryCustomImpl implements TripRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<TripInfoDto> findTripInfosWithDynamicSort(Long userId, LocalDate date, String sortField,
		String sortDirection, String type) {
		QTrip trip = QTrip.trip;
		QTripMember tripMember = QTripMember.tripMember;

		JPAQuery<TripInfoDto> query = jpaQueryFactory
			.select(
				Projections.bean(TripInfoDto.class,
					trip.id,
					trip.name,
					trip.type,
					trip.startDate,
					trip.endDate,
					trip.country,
					trip.thumbnail,
					trip.invitationCode,
					trip.labelColor
				)
			)
			.from(tripMember)
			.leftJoin(tripMember.trip, trip)
			.where(
				tripMember.user.id.eq(userId)
			);

		
		query.where(
			type.equals("incoming") ? trip.endDate.goe(date) : trip.endDate.lt(date)
		);

		addOrderByToQuery(sortField, sortDirection, query);
		
		return query.fetch();
	}

	private void addOrderByToQuery(String sortField, String sortDirection, JPAQuery<?> query) {
		//동적 정렬 추가 //검색기능 추가된다면 startDate로 조회할 수도 있음
		PathBuilder<?> entityPath = new PathBuilder<>(Trip.class, "trip");
		Order order = sortDirection.equalsIgnoreCase("ASC") ? Order.ASC : Order.DESC;
		OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, entityPath.get(sortField, LocalDate.class));

		query.orderBy(orderSpecifier);
	}

	@Override
	public List<TripInfoDto> findBookmarkTripInfosWithDynamicSort(Long userId, String sortField,
		String sortDirection) {
		QTrip trip = QTrip.trip;
		QBookmark bookmark = QBookmark.bookmark;

		JPAQuery<TripInfoDto> query = jpaQueryFactory
			.select(
				Projections.bean(TripInfoDto.class,
					trip.id,
					trip.name,
					trip.type,
					trip.startDate,
					trip.endDate,
					trip.country,
					trip.thumbnail,
					trip.invitationCode,
					trip.labelColor
				)
			)
			.from(bookmark)
			.leftJoin(bookmark.trip, trip)
			.where(
				bookmark.user.id.eq(userId)
			);

		addOrderByToQuery(sortField, sortDirection, query);

		return query.fetch();
	}

}
