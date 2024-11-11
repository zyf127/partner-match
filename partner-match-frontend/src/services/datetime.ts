import moment from 'moment-timezone';

export const formatDateTime = (dateTimeString: string) => {
    const dateTime = new Date(dateTimeString);
    const shanghaiMoment = moment(dateTime).tz('Asia/Shanghai');
    const shanghaiDateTime= shanghaiMoment.toDate();
    if (isNaN(shanghaiDateTime.getTime())) {
        return '';
    } else {
        return `${shanghaiDateTime.getFullYear()}年${(shanghaiDateTime.getMonth() + 1).toString().padStart(2, '0')}月${shanghaiDateTime.getDate().toString().padStart(2, '0')}日 ${shanghaiDateTime.getHours().toString().padStart(2, '0')}:${shanghaiDateTime.getMinutes().toString().padStart(2, '0')}`;
    }
}