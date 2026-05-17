
CREATE PROCEDURE check_ip_velocity
    @p_ip_address VARCHAR(45),
    @p_since DATETIME2,
    @p_tx_count INT OUTPUT
  AS
BEGIN
SELECT @p_tx_count = COUNT(*)
FROM transactions
WHERE ip_address = @p_ip_address
  AND transaction_time >= @p_since;
END;