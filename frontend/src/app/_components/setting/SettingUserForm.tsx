import { EventFor } from '@/types/type';
import React, {
  Dispatch,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from 'react';
import { ages } from './contants';

import Link from 'next/link';
import { checkIsExistNickname, patchFavoriteRegion } from './actions';
import { TMyInfo } from '@/types/response';
import SettingUserArea from './SettingUserArea';

export interface TProps {
  userForm: TMyInfo;
  fetchUserData: TMyInfo;
  setUserForm: Dispatch<SetStateAction<TMyInfo>>;
  openModal: () => void;
}

export default function SettingUserForm({
  userForm,
  fetchUserData,
  setUserForm,
  openModal,
}: TProps) {
  const [nicknameValidation, setNicknameValidation] = useState(false);
  const [completeBtnValidation, setCompleteBtnValidation] = useState(false);
  const [userRegions, setUserRegions] = useState(
    fetchUserData.favorite_regions,
  );
  const [errorText, setErrorText] = useState('');
  const nicknameRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setUserRegions(fetchUserData.favorite_regions);
  }, [fetchUserData]);

  const onChangeInput = (e: EventFor<'input', 'onChange'>) => {
    const { value, name } = e.target;
    setUserForm({ ...userForm, [name]: value });
  };

  const onClickInput = (e: EventFor<'button', 'onClick'>) => {
    const { value, name } = e.currentTarget;
    setUserForm({ ...userForm, [name]: value });
  };

  const compareNicknameIsExist = async () => {
    if (userForm.nickname === fetchUserData.nickname) return;
    if (!userForm.nickname) {
      setNicknameValidation(false);
      setErrorText('1자 이상 20자 이내로 입력해주세요.');
      return;
    }
    if (validationNickname(userForm.nickname)) {
      const { available: isNicknameAvailiable } = await checkIsExistNickname(
        userForm.nickname,
      );
      if (isNicknameAvailiable) setNicknameValidation(true);
      else {
        setNicknameValidation(false);
        setErrorText('이미 사용중인 닉네임입니다.');
      }
    } else {
      setErrorText('영문/한글 또는 숫자로만 1자이상 20자 이내로 입력해주세요.');
    }
  };

  const deleteInterestedArea = async (id: number) => {
    try {
      const restIds = userForm.favorite_regions
        .filter((region) => region.id !== id)
        .map((region) => region.id);
      patchFavoriteRegion(restIds);
    } catch (error) {
      console.log(error);
    } finally {
    }
  };

  useEffect(() => {
    if (
      userForm.age !== '없음' ||
      userForm.gender !== '없음' ||
      userForm.nickname !== fetchUserData.nickname ||
      userForm.profile_image_url !== fetchUserData.profile_image_url ||
      nicknameValidation
    )
      setCompleteBtnValidation(true);
  }, [userForm, setCompleteBtnValidation, fetchUserData, nicknameValidation]);

  return (
    <section className="mx-4">
      <label htmlFor="name" className="flex flex-col">
        <div className="flex justify-between items-center my-2  ">
          <span className="text-xl leading-5 text-gray-700">닉네임(필수)</span>
          <span className="text-gray-500">
            <span
              className={`${userForm.nickname.length > 20 && 'text-red-500'}`}
            >
              {userForm?.nickname?.length}{' '}
            </span>
            / 20
          </span>
        </div>
        <input
          ref={nicknameRef}
          id="name"
          type="text"
          name="nickname"
          placeholder="닉네임"
          value={userForm.nickname}
          className={`border-[1px] px-4 py-2 text-2xl rounded-md outline-gray-300 ${
            userForm.nickname === fetchUserData.nickname
              ? ''
              : userForm.nickname !== fetchUserData.nickname &&
                nicknameValidation
              ? 'border-main'
              : 'border-red-500'
          }`}
          onChange={onChangeInput}
          onBlur={compareNicknameIsExist}
        />
      </label>
      {userForm.nickname === fetchUserData.nickname ? (
        <></>
      ) : userForm.nickname !== fetchUserData.nickname && nicknameValidation ? (
        <div className="text-main">사용 가능한 닉네임입니다.</div>
      ) : (
        <div className="text-red-500">{errorText}</div>
      )}

      <section className="flex flex-col">
        <span className="text-gray-700 my-2">성별</span>
        <div className="flex items-center text-gray-300 text-2xl text-center gap-x-2">
          <button
            value="male"
            name="gender"
            className={`grow  rounded-md py-2 cursor-pointer ${
              userForm?.gender === 'male'
                ? 'border-main text-main border-[1px] font-semibold'
                : 'border-gray-300 border-[1px]'
            }`}
            onClick={onClickInput}
          >
            남성
          </button>
          <button
            type="button"
            value="female"
            name="gender"
            className={`grow border-gray-300 border-[1px] rounded-md py-2 cursor-pointer ${
              userForm?.gender === 'female'
                ? 'border-main text-main border-[1px] font-semibold'
                : 'border-gray-300 border-[1px]'
            }`}
            onClick={onClickInput}
          >
            여성
          </button>
        </div>
      </section>
      <section className="flex flex-col">
        <span className="text-gray-700 my-2">나이</span>
        <div className="grid grid-cols-3 gap-3">
          {ages.map(({ label, value }) => (
            <button
              key={label}
              value={value}
              name="age"
              className={`border-[1px] py-3 text-xl text-gray-300 rounded-md cursor-pointer
              ${
                userForm?.age === value
                  ? 'border-main text-main border-[1px] font-semibold'
                  : 'border-gray-300 border-[1px]'
              }`}
              onClick={onClickInput}
            >
              {label}
            </button>
          ))}
        </div>
      </section>
      <section className="flex flex-col">
        <span className="text-gray-700 my-2">관심 지역(선택)</span>
        <ul className="grid grid-cols-2 gap-x-2 gap-y-4">
          {Array.from(Array(5)).map((_, idx) => (
            <SettingUserArea
              userRegions={userRegions}
              idx={idx}
              key={idx}
              deleteInterestedArea={deleteInterestedArea}
            />
          ))}
          <Link
            href="/area"
            className="flex justify-center items-center gap-x-2 text-xl text-gray-500 bg-gray-200 font-semibold border-[1px] border-gray-300 py-4 leading-5 rounded-md"
          >
            지역 선택
          </Link>
        </ul>
      </section>
      <div className="pb-8 mt-[60px] mx-4 flex justify-center items-center text-white text-2xl leading-7 font-semibold">
        <button
          className={`w-full py-4 rounded-md ${
            completeBtnValidation ? 'bg-main' : 'bg-gray-200'
          }`}
          disabled={!completeBtnValidation}
          onClick={openModal}
        >
          완료
        </button>
      </div>
    </section>
  );
}

function validationNickname(nickname: string) {
  const regExp = /^[a-zA-Z0-9ㄱ-힣][a-zA-Z0-9ㄱ-힣 ]{0,19}$/;
  console.log(regExp.test(nickname));
  return regExp.test(nickname);
}
