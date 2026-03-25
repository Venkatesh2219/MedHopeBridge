package com.medibridge.repository;

import com.medibridge.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByUserId(Long userId);
    List<Medicine> findByDonationStatus(Medicine.DonationStatus status);
    List<Medicine> findByUserIdAndDonationStatus(Long userId, Medicine.DonationStatus status);

    Page<Medicine> findByUserId(Long userId, Pageable pageable);
    Page<Medicine> findByDonationStatus(Medicine.DonationStatus status, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.expiryDate <= :targetDate AND m.expiryDate >= :today")
    List<Medicine> findExpiringSoon(@Param("userId") Long userId,
                                    @Param("today") LocalDate today,
                                    @Param("targetDate") LocalDate targetDate);

    @Query("SELECT m FROM Medicine m WHERE m.expiryDate <= :targetDate AND m.expiryDate >= :today")
    List<Medicine> findAllExpiringSoon(@Param("today") LocalDate today,
                                       @Param("targetDate") LocalDate targetDate);

    @Query("SELECT m FROM Medicine m WHERE m.expiryDate < :today")
    List<Medicine> findAllExpired(@Param("today") LocalDate today);

    @Query("""
            SELECT m FROM Medicine m
            WHERE m.donationStatus = 'AVAILABLE_TO_DONATE'
              AND m.latitude  BETWEEN :minLat AND :maxLat
              AND m.longitude BETWEEN :minLng AND :maxLng
            """)
    List<Medicine> findNearbyDonations(@Param("minLat") double minLat,
                                        @Param("maxLat") double maxLat,
                                        @Param("minLng") double minLng,
                                        @Param("maxLng") double maxLng);

    long countByDonationStatus(Medicine.DonationStatus status);
    long countByUserId(Long userId);

    // ── Search donations by name or type ──────────────────
    @Query("""
            SELECT m FROM Medicine m
            WHERE m.donationStatus = 'AVAILABLE_TO_DONATE'
              AND (LOWER(m.name) LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(m.type) LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(m.notes) LIKE LOWER(CONCAT('%',:q,'%')))
            ORDER BY m.createdAt DESC
            """)
    List<Medicine> searchDonations(@Param("q") String query);
}
