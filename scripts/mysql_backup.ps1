# A sample prog to create a mysql database backup and rotate backups for x number of days
# Note:  the target database and mysql admin passwd is passed to the script as parameters
# typical usage:  ./mysql_backup.ps1 sometargetdatabasename mysqladminpassword

param (
[string] $db,
[string] $passwd
)

$BACKUPDIR = "C:\mysqlbackup"
$TS = Get-Date -Format "yyyyMMdd_HHmmss"
$filename = "$DB_$TS.sql"
$targetpath = Join-Path $BACKUPDIR $filename
$logpath = Join-Path $BACKUPDIR "log.txt"
$rotation = 7


if (-not($db)) { throw "You must supply a target databasename" }
if (-not($passwd)) { throw "You must supply a database admin password" }


If (-not (Test-Path $BACKUPDIR)) {
    try {
    New-Item -Path $BACKUPDIR -ItemType Directory | Out-Null
    } catch {
     add-content -path $logpath -value "$TS Cannot find nor create backup dir:  $BACKUPDIR"
     add-content -path $logpath -value "$TS Error:  $($_.Exception.Message)"
     exit 1
    }
}




add-content -path $logpath -value "$TS Creating backup of database $db to $targetpath"
try {
    & "mysqldump" --user="root" --password="$passwd" --databases "$DB" --result-file="$targetpath"
    add-content -path $logpath -value "$TS Backup created successfully"
} catch {
    add-content -path $logpath -value "$TS Error during mysqldump:  $($_.Exception.Message)"
    exit 1
}



add-content -path $logpath -value "$TS Performing backup rotation"
$cutoffDate = (Get-Date).AddDays(-$rotation)

Get-ChildItem -Path $BACKUPDIR -Filter "*.sql" | ForEach-Object {
    if ($_.CreationTime -lt $cutoffDate) {
	add-content -path $logpath -value "$TS Deleting old backup:  $($_.Name)"
        Remove-Item $_.FullName -Force
    }
}
add-content -path $logpath -value "$TS backup rotation complete"




