import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createBooking, cancelBooking } from '../booking'

vi.mock('../../utils/request', () => {
  return {
    default: {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
    }
  }
})

import request from '../../utils/request'

describe('booking api', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should_createBookingSuccessfully_when_roomIsAvailable', async () => {
    const bookingData = {
      roomId: 1,
      memberId: 1,
      checkInDate: '2026-06-01',
      checkOutDate: '2026-06-03',
      deposit: 300,
      remark: '测试预订'
    }

    const mockResponse = {
      code: 200,
      data: {
        id: 1,
        roomId: 1,
        memberId: 1,
        checkInDate: '2026-06-01',
        checkOutDate: '2026-06-03',
        status: '已预订',
        deposit: 300,
        remark: '测试预订'
      }
    }

    request.post.mockResolvedValue(mockResponse)

    const result = await createBooking(bookingData)

    expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
    expect(result.data.status).toBe('已预订')
    expect(result.data.roomId).toBe(1)
    expect(result.data.memberId).toBe(1)
  })

  it('should_returnConflictError_when_roomIsAlreadyBooked', async () => {
    const bookingData = {
      roomId: 1,
      memberId: 1,
      checkInDate: '2026-06-01',
      checkOutDate: '2026-06-03',
      deposit: 300
    }

    const conflictError = new Error('客房当前不可预订')
    conflictError.response = { status: 409, data: { message: '客房当前不可预订' } }

    request.post.mockRejectedValue(conflictError)

    await expect(createBooking(bookingData)).rejects.toThrow('客房当前不可预订')
    expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
  })

  it('should_returnValidationError_when_checkInDateIsAfterCheckOutDate', async () => {
    const bookingData = {
      roomId: 1,
      memberId: 1,
      checkInDate: '2026-06-05',
      checkOutDate: '2026-06-03',
      deposit: 300
    }

    const validationError = new Error('退房日期不能早于入住日期')
    validationError.response = { status: 400, data: { message: '退房日期不能早于入住日期' } }

    request.post.mockRejectedValue(validationError)

    await expect(createBooking(bookingData)).rejects.toThrow('退房日期不能早于入住日期')
    expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
  })

  it('should_cancelBookingSuccessfully_when_bookingIsActive', async () => {
    const mockResponse = {
      code: 200,
      data: {
        id: 1,
        roomId: 1,
        memberId: 1,
        status: '已取消'
      }
    }

    request.put.mockResolvedValue(mockResponse)

    const result = await cancelBooking(1)

    expect(request.put).toHaveBeenCalledWith('/bookings/1/cancel')
    expect(result.data.status).toBe('已取消')
  })
})
