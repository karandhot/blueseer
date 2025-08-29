#!/bin/bash

set -o pipefail

# MySQL Credentials
DB_USER="root"
DB_NAME=$2 # Or use --all-databases for all databases
export MYSQL_PWD=$1

config_file="bs_backup.cfg"
declare -a db_array

# lets check for config file...if not available bail
if [[ ! -f "$config_file" ]]; then
 echo "no config file found...exiting"
 echo "config file...bs_backup.cfg...required of format cust,dbname"
 exit 1
fi


# Backup Directory
BACKUP_DIR="./backupdir"

process_db() {
# Timestamp for backup file
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Backup file name
#BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql.gz"
BACKUP_FILE="${BACKUP_DIR}/$1_${TIMESTAMP}.sql.gz"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

MYCOMMAND="mysqldump -u ${DB_USER} -p${DB_PASSWORD} $1 | gzip > ${BACKUP_FILE}"
# Perform the backup using mysqldump and compress it
#myerror=$({ mysqldump -u"${DB_USER}" -p"${DB_PASSWORD}" "$1" | gzip > "${BACKUP_FILE}" ; } 2>&1 )
myerror=$({ mysqldump -u"${DB_USER}" "$1" | gzip > "${BACKUP_FILE}" ; } 2>&1 )
#myerror=$(eval "$MYCOMMAND" 1>/dev/null 2>&1)


# Check if backup was successful
if [ $? -eq 0 ]; then
echo "MySQL backup of $1 created successfully at ${BACKUP_FILE}"
else
echo "Error: MySQL backup of $1 failed.  reason: $myerror"
fi
}

# absorb config file...setting databases that will be backed up
while IFS= read -r line; do
db_array+=($line)
done < "$config_file"

# now read array
for db in "${db_array[@]}"; do
process_db $(echo $db |cut -d',' -f2)
done


# Optional: Delete old backups (e.g., keep last 7 days)
# find "${BACKUP_DIR}" -type f -name "*.sql.gz" -mtime +7 -delete

