import PlaceDetail from '@/app/_components/place/PlaceDetail';
import { getPlaceDetail } from '../action';
import { authenticateUser } from '@/app/_components/mypage/hooks/myPageActions';
import { myInfoSchema } from '@/types/response';

export default async function PlaceDetailPage({
  params,
}: {
  params: { id: string };
}) {
  const memberJson = await authenticateUser();
  const memberInfo = myInfoSchema.safeParse(memberJson);

  const detail = await getPlaceDetail(params.id);

  if (!detail.success) {
    console.log({ message: detail.error.message });

    return <main>Failed</main>;
  }

  return <PlaceDetail detail={detail.data} isLoggedIn={memberInfo.success} />;
}
