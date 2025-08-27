#!/bin/bash

# MySQL Credentials
DB_USER="root"
DB_PASSWORD=$1
DB_NAME=$2 # Or use --all-databases for all databases

# Backup Directory
BACKUP_DIR="./backupdir"

# Timestamp for backup file
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Backup file name
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql.gz"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Perform the backup using mysqldump and compress it
mysqldump -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" | gzip > "${BACKUP_FILE}"

# Check if backup was successful
if [ $? -eq 0 ]; then
echo "MySQL backup of ${DB_NAME} created successfully at ${BACKUP_FILE}"
else
echo "Error: MySQL backup of ${DB_NAME} failed."
fi

# Optional: Delete old backups (e.g., keep last 7 days)
# find "${BACKUP_DIR}" -type f -name "*.sql.gz" -mtime +7 -delete
