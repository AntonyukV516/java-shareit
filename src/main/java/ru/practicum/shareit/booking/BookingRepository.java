package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId" +
            " AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId" +
            " AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND ((b.start BETWEEN :start AND :end) OR (b.end BETWEEN :start AND :end))")
    boolean existsApprovedBookingsForItemBetweenDates(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.end < :now AND b.status = 'APPROVED' ORDER BY b.end DESC")
    List<Booking> findLastBookings(@Param("itemId") Long itemId,
                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start > :now AND b.status = 'APPROVED' ORDER BY b.start ASC")
    List<Booking> findNextBookings(@Param("itemId") Long itemId,
                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds " +
            "AND b.end < :now AND b.status = 'APPROVED' ORDER BY b.item.id, b.end DESC")
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds " +
            "AND b.start > :now AND b.status = 'APPROVED' ORDER BY b.item.id, b.start ASC")
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId AND b.end < :now AND b.status = 'APPROVED'")
    boolean existsByBookerIdAndItemIdAndEndBefore(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now);

    default Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now) {
        return findLastBookings(itemId, now).stream().findFirst().orElse(null);
    }

    default Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now) {
        return findNextBookings(itemId, now).stream().findFirst().orElse(null);
    }
}
