package com.tenmo.repository;

import com.tenmo.dto.transfer.TransferResponseDto;
import com.tenmo.entity.Account;
import com.tenmo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query(value = "UPDATE account ac " +
            "SET ac.balance = CASE " +
            "   WHEN ac.account_id = :accountFromId THEN ac.balance - :amount " +
            "   WHEN ac.account_id = :accountToId THEN ac.balance + :amount " +
            "END " +
            "WHERE ac.account_id IN (:accountFromId, :accountToId) ", nativeQuery = true)
    void handleDirectTransfer(@Param("accountFromId") Long accountFromId,
                              @Param("accountToId") Long accountToId,
                              @Param("amount") BigDecimal amount);


    @Query(value = "SELECT " +
            "tr.transfer_id AS transferId, " +
            "sender.username AS accountFromUsername, " +
            "recipient.username AS accountToUsername, " +
            "tr.amount, " +
            "tr.transfer_message AS transferMessage, " +
            "tr.currency, " +
            "tr.created_at AS createdAt, " +
            "tt.transfer_type_desc AS transferTypeDescription," +
            "ts.transfer_status_desc AS transferStatusDescription," +
            "tt.transfer_type_id AS typeId," +
            "ts.transfer_status_id AS statusId" +
            "FROM transfer tr " +
            "JOIN account sender_ac ON sender_ac.account_id = tr.account_from " +
            "JOIN tenmo_user sender ON sender.user_id = sender_ac.user_id " +
            "JOIN account recipient_ac ON recipient_ac.account_id = tr.account_to " +
            "JOIN tenmo_user recipient ON recipient.user_id = recipient_ac.user_id " +
            "JOIN transfer_type tt ON tt.transfer_type_id = tr.transfer_type_id " +
            "JOIN transfer_status ts ON ts.transfer_status_id = tr.transfer_status_id" +
            "WHERE tr.account_from = :accountId OR tr.account_to = :accountId"
            , nativeQuery = true)
    List<TransferResponseDto> accountTransferHistory(@Param("accountId") Long accountId);

    @Query(value = "SELECT * FROM tenmo_user tu " +
            "JOIN account ac ON ac.user_id = tu.user_id " +
            "WHERE account_id = :accountId LIMIT 1; ", nativeQuery = true)
    Optional<User> findUserByAccountId(@Param("accountId")Long accountId);

}
