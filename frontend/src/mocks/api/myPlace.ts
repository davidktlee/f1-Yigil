import { http, HttpResponse } from 'msw';
import { myPlaceData } from './data/myPlaceData';

const handlers = [
  http.get(`http://localhost:8080/api/my/posts`, ({ request }) => {
    return HttpResponse.json({
      status: 200,
      data: myPlaceData,
    });
  }),
];
export default handlers;