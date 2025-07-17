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
$rotation = 30


if (-not($db)) { throw "You must supply a target databasename" }
if (-not($passwd)) { throw "You must supply a database admin password" }


If (-not (Test-Path $BACKUPDIR)) {
    try {
    New-Item -Path $BACKUPDIR -ItemType Directory | Out-Null
    } catch {
     write-error "Cannot find nor create backup dir:  $BACKUPDIR"
     write-error "Error: $($_.Exception.Message)"
     exit 1
    }
}

$passwd = "-p" + $passwd.trim()



Write-Host "Creating backup of database '$db' to '$targetpath'..."
try {
    & "mysqldump" --user="root" --password="$passwd" --databases "$DB" --result-file="$targetpath"
    Write-Host "Backup created successfully."
} catch {
    Write-Error "Error during mysqldump: $($_.Exception.Message)"
    exit 1
}



Write-Host "Performing backup rotation..."
$cutoffDate = (Get-Date).AddDays(-$rotation)

Get-ChildItem -Path $BACKUPDIR -Filter "*.sql" | ForEach-Object {
    if ($_.CreationTime -lt $cutoffDate) {
        Write-Host "Deleting old backup: $($_.Name)"
        Remove-Item $_.FullName -Force
    }
}
Write-Host "Backup rotation complete."




