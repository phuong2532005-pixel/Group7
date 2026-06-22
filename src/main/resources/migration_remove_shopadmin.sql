-- Migration: remove ShopAdmin role and enforce single-company model
-- Target DB: SQL Server
-- Run on staging first, then production.

BEGIN TRANSACTION;

-- 1) Audit current role distribution
SELECT role_id, COUNT(*) AS total_users
FROM Users
GROUP BY role_id
ORDER BY role_id;

-- 2) Disable legacy ShopAdmin accounts (role_id = 2)
-- Option A (recommended): convert to Admin
UPDATE Users
SET role_id = 1
WHERE role_id = 2;

-- Option B (alternative): lock old ShopAdmin accounts instead
-- UPDATE Users
-- SET status = 0
-- WHERE role_id = 2;

-- 3) Ensure only one Director account remains (role_id = 4)
-- Replace @DirectorUserId with the account you want to keep as Director.
DECLARE @DirectorUserId INT = NULL;

-- Example:
-- SET @DirectorUserId = 10;

IF @DirectorUserId IS NOT NULL
BEGIN
    UPDATE Users
    SET role_id = 1
    WHERE role_id = 4
      AND id <> @DirectorUserId;
END;

-- 4) Ensure stock column exists for centralized inventory model.
IF COL_LENGTH('ProductVariants', 'stock') IS NULL
BEGIN
    ALTER TABLE ProductVariants
    ADD stock INT NOT NULL CONSTRAINT DF_ProductVariants_stock DEFAULT(0);
END;

-- 5) Drop legacy branch inventory table if still exists.
IF OBJECT_ID('dbo.ShopInventory', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.ShopInventory;
END;

-- 6) Drop legacy branch table if still exists.
IF OBJECT_ID('dbo.Shops', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.Shops;
END;

-- 7) Post-migration checks
SELECT role_id, COUNT(*) AS total_users
FROM Users
GROUP BY role_id
ORDER BY role_id;

SELECT COUNT(*) AS director_count
FROM Users
WHERE role_id = 4;

IF COL_LENGTH('ProductVariants', 'stock') IS NOT NULL
BEGIN
    SELECT TOP 20 id, product_id, stock
    FROM ProductVariants
    ORDER BY id;
END;

COMMIT TRANSACTION;

-- If needed:
-- ROLLBACK TRANSACTION;
