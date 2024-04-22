export const formatDateTime = (dateTimeString: string) => {
    const dateTime = new Date(dateTimeString);
    return `${dateTime.getFullYear()}年${(dateTime.getMonth() + 1).toString().padStart(2, '0')}月${dateTime.getDate().toString().padStart(2, '0')}日 
    ${dateTime.getHours().toString().padStart(2, '0')}:${dateTime.getMinutes().toString().padStart(2, '0')}`;
}